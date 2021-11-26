package com.myworld.cauth.secure.security.validateCode.common.entity;

import java.time.LocalDateTime;

/**
 * 因父类ValidateCode已添加了serializable，所以子类就不用再添加了
 * 但是序列化时，子类必须要有一个自己的空的构造函数
 */
public class SmsCode extends ValidateCode {

    private String mobile;

    public SmsCode() {
    }

    public SmsCode(String mobile, String code, LocalDateTime expireTime, String sessionId){
        super(code, expireTime, sessionId);
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}

