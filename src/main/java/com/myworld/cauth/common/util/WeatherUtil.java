package com.myworld.cauth.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.myworld.cauth.common.properties.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class WeatherUtil {

    private static String cityCodes = null;
    private static String url_prefix = "http://d1.weather.com.cn/sk_2d/";
    private static String referer_prefix = "http://www.weather.com.cn/weather1d/";

    public static String getCityCodes() {
        if (null == cityCodes) {
            synchronized (HttpUtil.class) {
                if (null == cityCodes) {
                    cityCodes = init();
                }
            }
        }
        return cityCodes;
    }

    private static String init() {
        String newCityCodes = null;
        Resource resource = new ClassPathResource("weatherCity.json");
        try {
            InputStream inputStream = resource.getInputStream();
            List<String> strList = IOUtils.readLines(inputStream, "utf8");
            StringBuilder content = new StringBuilder();
            for(String line : strList){
                content.append(line);
            }
            newCityCodes = content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newCityCodes;
    }

    public static String getCityCodeByCityName(String cityName) {
        String idNeed = null;
        String cityCodesContent = getCityCodes();
        JSONArray jsonArray = JSONArray.parseArray(cityCodesContent);
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            String theName = jsonObject.getString("name");
            String theId = jsonObject.getString("id");
            if(theName.contains(cityName)){
                idNeed = theId;
                break;
            }
        }
        log.info("城市名称是：{}, 获取到的id是：{}", cityName, idNeed);
        return idNeed;
    }


    public static String getWeatherByCityName(String cityName) {
        String weatherStr = null;
        String idNeed = getCityCodeByCityName(cityName);
        if(idNeed != null) {
            String url = url_prefix + idNeed + ".html";
            String referer = referer_prefix + idNeed + ".shtml";

            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", SecurityConstants.DEFAULT_USER_AGENT);
            headers.put("Referer", referer);

            long currentTime = new Date().getTime();
            weatherStr = HttpUtil.sendGetRequestWithHeaders(url, "_=" + currentTime, headers);
        }
        log.info("返回的天气数据是：{}", weatherStr);
        return weatherStr;
    }
}
