package com.myworld.cauth.siteinfo.service;

import com.myworld.cauth.siteinfo.entity.Writing;
import com.myworld.cauth.common.model.ApiResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface WritingService {

    ApiResult adminGetWritingList(String type);
    ApiResult adminCreateWriting(String type, String title);
    ApiResult adminGetWriting(String idDetail);
    ApiResult adminSetWriting(Writing writing);
    ApiResult adminDelWriting(String idDetail);


    ApiResult getSiteInfo();
    ApiResult getSiteInfoReleaseTime(String title);

}
