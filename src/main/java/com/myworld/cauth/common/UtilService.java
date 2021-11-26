package com.myworld.cauth.common;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Set;

public interface UtilService {
    Integer getYearNow();
    Integer getYearMonthNow();
    Integer getYearMonthDayNow();
    Integer getCustomDateFromTimestamp(Timestamp timestamp, Integer start, Integer end);

    Integer getMonthNow();
    Integer getLastMonth();
    int getPreviousTwelveNumber(int number);

    String getIpAddress(HttpServletRequest request);
    String getRegionOnIpAddress(String ip);
    String shortCityName(String cityName);

    Boolean isNumeric(String str);
    Boolean isInteger(String str);
    Boolean isLetter(String str);
    Boolean isZhCN(String str);
    Boolean isWid(String str);

    Set<String> getImgStr(String htmlStr);

    byte[] base64ToBytes(String base64);
    String bytesToBase64(byte[] bytes);

}
