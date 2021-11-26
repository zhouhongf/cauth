package com.myworld.cauth.secure.security.validateCode.common.service;

import com.myworld.cauth.common.model.ApiResult;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 验证码处理器，封装不同的验证码处理逻辑
 */
public interface ValidateCodeProcessor {

    /**
     * 创建校验码
     */
    void create(ServletWebRequest request) throws Exception;

    /**
     * 校验验证码
     */
    ApiResult validate(ServletWebRequest servletWebRequest);

}
