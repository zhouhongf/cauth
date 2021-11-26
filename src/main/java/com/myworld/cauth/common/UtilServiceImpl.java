package com.myworld.cauth.common;


import com.myworld.cauth.common.util.IPUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;


@Service
public class UtilServiceImpl implements UtilService {
    private static Logger log = LogManager.getRootLogger();
    @Override
    public Integer getYearNow() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return this.getCustomDateFromTimestamp(timestamp, 0, 4);
    }

    @Override
    public Integer getYearMonthNow() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return this.getCustomDateFromTimestamp(timestamp, 0, 6);
    }

    @Override
    public Integer getYearMonthDayNow() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return this.getCustomDateFromTimestamp(timestamp, 0, 8);
    }

    @Override
    public Integer getCustomDateFromTimestamp(Timestamp timestamp, Integer start, Integer end) {
        String timeNow = new SimpleDateFormat("yyyyMMddHHmmss").format(timestamp);
        return Integer.parseInt(timeNow.substring(start, end));
    }


    @Override
    public Integer getMonthNow() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return this.getCustomDateFromTimestamp(timestamp, 4, 6);
    }

    @Override
    public Integer getLastMonth() {
        Integer monthNow = this.getMonthNow();
        int lastMonth;
        if (monthNow.equals(1)){
            lastMonth = 12;
        }else {
            lastMonth = monthNow - 1;
        }
        return lastMonth;
    }

    @Override
    public int getPreviousTwelveNumber(int number) {
        int num;
        switch (number) {
            case 1:
                num = 12;
                break;
            case 2:
                num = 1;
                break;
            case 3:
                num = 2;
                break;
            case 4:
                num = 3;
                break;
            case 5:
                num = 4;
                break;
            case 6:
                num = 5;
                break;
            case 7:
                num = 6;
                break;
            case 8:
                num = 7;
                break;
            case 9:
                num = 8;
                break;
            case 10:
                num = 9;
                break;
            case 11:
                num = 10;
                break;
            case 12:
                num = 11;
                break;
            default:
                num = 0;
        }
        return num;
    }


    @Override
    public String getIpAddress(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        log.info("ip地址为：" + ip);
        return ip;
    }

    @Override
    public String getRegionOnIpAddress(String ip) {
        String fullName = IPUtil.getCityInfo(ip);
        if (fullName == null) {
            log.info("cannot get city location or name" );
            return null;
        }
        log.info("ip belongs cityname:" + fullName);
        return fullName;
    }

    @Override
    public String shortCityName(String cityName) {
        // 如果城市中包含 市 区 县， 则去掉市 区 县 的后缀
        String theCityName;
        Pattern pattern = Pattern.compile("([\\u4e00-\\u9fa5]{2,})[市|区|县]");
        Matcher m = pattern.matcher(cityName);
        if (m.find()){
            theCityName = m.group(1);
        } else {
            theCityName = cityName;
        }
        return theCityName;
    }

    @Override
    public Boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^(-|\\+)?\\d+(\\.\\d+)?$");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    @Override
    public Boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher isInt = pattern.matcher(str);
        return isInt.matches();
    }

    @Override
    public Boolean isLetter(String str) {
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        Matcher isLet = pattern.matcher(str);
        return isLet.matches();
    }

    @Override
    public Boolean isZhCN(String str) {
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]+$");
        Matcher isZh = pattern.matcher(str);
        return isZh.matches();
    }

    @Override
    public Boolean isWid(String str) {
        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{6,18}$");
        Matcher isWid = pattern.matcher(str);
        return isWid.matches();
    }


    /**
     * 利用正则表达式，获取tinymce中的图片链接地址
     */
    @Override
    public Set<String> getImgStr(String htmlStr) {
        Set<String> pics = new HashSet<>();
        String img = "";
        Pattern p_image;
        Matcher m_image;
        //     String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            // 得到<img />数据
            img = m_image.group();
            // 匹配<img>中的src数据
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        log.info("【提取出来的图片URL是】" + pics);
        return pics;
    }


    @Override
    public byte[] base64ToBytes(String base64) {
        base64 = base64.replaceAll("data:image/jpeg;base64,", "");
        return Base64.decodeBase64(base64);
    }

    @Override
    public String bytesToBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }


}
