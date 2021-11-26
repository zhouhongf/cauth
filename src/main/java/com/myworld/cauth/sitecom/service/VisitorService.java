package com.myworld.cauth.sitecom.service;


import com.myworld.cauth.common.model.ApiResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface VisitorService {

    ApiResult updateVisitor(HttpServletRequest request, HttpServletResponse response);
    ApiResult updateVisitorMobile(String personStr, HttpServletRequest request, HttpServletResponse response);
    void saveVisitor(String ipAddress, String deviceType, String provinceName, String cityName, HttpServletRequest request);

    ApiResult getVisitorList(Integer pageSize, Integer pageIndex);

    String getCityLngLatByProvinceAndCity(String provinceName, String cityName);
}
