package com.myworld.cauth.secure.security.validateCode.common.service;

import com.myworld.cauth.secure.security.validateCode.common.entity.ValidateCode;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 验证码生成器
 */
public interface ValidateCodeGenerator {
    /**
     * 生成验证码
     */
    ValidateCode generate(ServletWebRequest request);
}
