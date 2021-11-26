package com.myworld.cauth.common.util;

import com.myworld.cauth.common.model.ApiResult;

public class ResultUtil {

    public static ApiResult success() {
        return success(null);
    }

    public static ApiResult success(Object object) {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(0);
        apiResult.setMsg("成功");
        apiResult.setData(object);
        return apiResult;
    }

    public static ApiResult success(String msg, Object object){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(0);
        apiResult.setMsg(msg);
        apiResult.setData(object);
        return apiResult;
    }

    public static ApiResult success(Long num, Object object){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(0);
        apiResult.setMsg("成功");
        apiResult.setNum(num);
        apiResult.setData(object);
        return apiResult;
    }

    public static ApiResult success(String msg, Long num, Object object){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(0);
        apiResult.setMsg(msg);
        apiResult.setNum(num);
        apiResult.setData(object);
        return apiResult;
    }

    public static ApiResult error(Integer code, String msg) {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
