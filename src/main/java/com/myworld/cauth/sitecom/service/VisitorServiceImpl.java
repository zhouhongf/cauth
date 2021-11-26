package com.myworld.cauth.sitecom.service;

import com.alibaba.fastjson.JSONObject;
import com.myworld.cauth.secure.service.SysUserService;
import com.myworld.cauth.sitecom.entity.Pcds;
import com.myworld.cauth.sitecom.entity.Visitor;
import com.myworld.cauth.sitecom.repository.BankRepository;
import com.myworld.cauth.sitecom.repository.PcdsRepository;
import com.myworld.cauth.sitecom.repository.VisitorRepository;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.util.StringUtil;
import com.myworld.cauth.common.UtilService;
import com.myworld.cauth.common.util.ResultUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Service
public class VisitorServiceImpl implements VisitorService {
    private static Logger log = LogManager.getRootLogger();
    private static final String DEVICE_MOBILE = "MOBILE";
    private static final String DEVICE_BROWSER = "BROWSE";

    private VisitorRepository visitorRepository;
    private PcdsRepository pcdsRepository;

    private SysUserService sysUserService;
    private UtilService utilService;

    public VisitorServiceImpl(VisitorRepository visitorRepository, PcdsRepository pcdsRepository, SysUserService sysUserService, UtilService utilService) {
        this.visitorRepository = visitorRepository;
        this.pcdsRepository = pcdsRepository;
        this.sysUserService = sysUserService;
        this.utilService = utilService;
    }

    @Override
    public ApiResult updateVisitor(HttpServletRequest request, HttpServletResponse response) {
        log.info("【开始执行updateVisitor方法】");
        String ipAddress = this.utilService.getIpAddress(request);
        String region = utilService.getRegionOnIpAddress(ipAddress);
        String provinceName = "";
        String cityName = "";
        if (region != null) {
            String[] theNames = region.split("\\|");
            provinceName = theNames[2];
            cityName = theNames[3];
        }

        this.saveVisitor(ipAddress, DEVICE_BROWSER, provinceName, cityName, request);

        // 以下用于返回给前端 城市名称 和 经纬度
        if (cityName.equals("内网IP") || cityName.equals("")) {
            cityName = "苏州市";
        }
        if (provinceName.equals("内网IP") || provinceName.equals("")) {
            provinceName = "江苏省";
        }
        String lngLat = this.getCityLngLatByProvinceAndCity(provinceName, cityName);

        String city = StringUtil.filterPlaceName(cityName);
        // String res = WeatherUtil.getWeatherByCityName(city);
        // String temp = "";
        // String weather = "";
        // if (res != null) {
        //    String strContent = res.substring(12);
        //    JSONObject jsonObject = JSONObject.parseObject(strContent);
        //    temp = jsonObject.getString("temp");
        //    weather = jsonObject.getString("weather");
        // }

        Map<String, String> map = new HashMap<>();
        map.put("city", city);
        map.put("lngLat", lngLat);
        // map.put("temp", temp);
        // map.put("weather", weather);
        return ResultUtil.success(map);
    }

    @Override
    public void saveVisitor(String ipAddress, String deviceType, String provinceName, String cityName, HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        String referer = request.getHeader("referer");
        String token = request.getHeader("Authorization");

        Integer yearMonth = this.utilService.getYearMonthNow();
        Integer yearMonthDay = this.utilService.getYearMonthDayNow();
        String idDetail = ipAddress + DEVICE_BROWSER + yearMonthDay;

        Long timeNow = new Date().getTime();
        Visitor visitor = visitorRepository.findByIdDetail(idDetail);
        if (visitor == null) {
            visitor = new Visitor();
            visitor.setIdDetail(idDetail);
            visitor.setIpAddress(ipAddress);
            visitor.setYearMonthInt(yearMonth);
            visitor.setDeviceType(deviceType);
            visitor.setUserAgent(userAgent);
            visitor.setReferer(referer);

            visitor.setProvince(provinceName);
            visitor.setCity(cityName);

            visitor.setVisitCount(0L);
        }

        // 如果一开始用户名为空值，则用户登录后，第二次登录，便可以通过解析token获得用户名
        String username = visitor.getUsername();
        if (username == null) {
            if (token != null) {
                username = this.sysUserService.tokenToUsername(token);
                if (username != null) {
                    visitor.setUsername(username);
                }
            }
        }

        // 一天当中，同一个IP地址的访问，仅在数据库中更新visitCount和updateTimeSum
        Long visitCount = visitor.getVisitCount() + 1L;
        visitor.setVisitCount(visitCount);

        String updateTimeSum = visitor.getUpdateTimeSum();
        if (updateTimeSum == null) {
            updateTimeSum = String.valueOf(timeNow);
        } else {
            updateTimeSum = updateTimeSum + '+' + timeNow;
        }
        visitor.setUpdateTimeSum(updateTimeSum);
        visitorRepository.save(visitor);
    }


    @Override
    public ApiResult updateVisitorMobile(String personStr, HttpServletRequest request, HttpServletResponse response) {
        log.info("【开始执行updateVisitorMobile方法】");
        JSONObject jsonObject =JSONObject.parseObject(personStr);
        Visitor theVisitor = JSONObject.toJavaObject(jsonObject, Visitor.class);
        String ipAddress = this.utilService.getIpAddress(request);

        this.saveVisitor(ipAddress, DEVICE_MOBILE, theVisitor.getProvince(), theVisitor.getCity(), request);

        Integer yearMonthDay = this.utilService.getYearMonthDayNow();
        String idDetail = ipAddress + DEVICE_MOBILE + yearMonthDay;

        Visitor visitor = visitorRepository.findByIdDetail(idDetail);
        visitor.setLongitude(theVisitor.getLongitude());
        visitor.setLatitude(theVisitor.getLatitude());
        visitor.setProvince(theVisitor.getProvince());
        visitor.setCity(theVisitor.getCity());
        visitor.setDistrict(theVisitor.getDistrict());
        visitor.setTownship(theVisitor.getTownship());
        visitor.setStreet(theVisitor.getStreet());
        visitor.setStreetNumber(theVisitor.getStreetNumber());
        visitor.setFormattedAddress(theVisitor.getFormattedAddress());
        visitorRepository.save(visitor);

        return ResultUtil.success();
    }


    @Override
    public ApiResult getVisitorList(Integer pageSize, Integer pageIndex){
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime");
        Page<Visitor> visitors = visitorRepository.findAll(pageable);
        Long theNum = visitors.getTotalElements();
        List<Visitor> list = visitors.getContent();
        return ResultUtil.success(theNum, list);
    }


    @Override
    public String getCityLngLatByProvinceAndCity(String provinceName, String cityName) {
        if (cityName == null) {
            return null;
        }
        // 先单独通过cityName去找
        List<Pcds> cities = pcdsRepository.findByNameAndLevel(cityName, "city");
        if (cities.size() == 1) {
            return cities.get(0).getCenter();
        }

        // 找不到，再通过provinceName-cityName去找
        if (provinceName != null) {
            String fullname = provinceName + '-' + cityName;
            Pcds city = pcdsRepository.findByFullname(fullname);
            if (city == null) {
                return null;
            }
            return city.getCenter();
        }
        // 否则就返回null
        return null;
    }
}
