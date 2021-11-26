package com.myworld.cauth.sitecom.service;

import com.myworld.cauth.common.model.ApiResult;

import java.util.Set;

public interface BankService {

    ApiResult getBankLocations(String cityname, String bankName);
    Set<String> getBankNamesByCityname(String cityname);
}
