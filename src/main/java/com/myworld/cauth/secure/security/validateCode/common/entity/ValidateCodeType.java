package com.myworld.cauth.secure.security.validateCode.common.entity;


import com.myworld.cauth.common.properties.SecurityConstants;

/**
 * Created on 2018/1/10.
 */
public enum ValidateCodeType {

    /**
     * 短信验证码, DEFAULT_PARAMETER_NAME_CODE_SMS = "smsCode"
     */
    SMS {
        @Override
        public String getParamNameOnValidate() {
            return SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_SMS;
        }
    },
    /**
     * 图片验证码, DEFAULT_PARAMETER_NAME_CODE_IMAGE = "imageCode"
     */
    IMAGE {
        @Override
        public String getParamNameOnValidate() {
            return SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_IMAGE;
        }
    };

    /**
     * 校验时从请求中获取的参数的名字
     */
    public abstract String getParamNameOnValidate();
}
