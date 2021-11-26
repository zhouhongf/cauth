package com.myworld.cauth.secure.security.validateCode.sms;

/**
 * 短信发送接口
 */
public interface SmsCodeSender {

    /**
     * 发送短信验证码
     */
    void send(String mobile, String code);
}
