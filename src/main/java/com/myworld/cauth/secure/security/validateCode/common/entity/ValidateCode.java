package com.myworld.cauth.secure.security.validateCode.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码信息封装类
 */
public class ValidateCode implements Serializable {

    private String code;

    private LocalDateTime expireTime;

    // 用于校验是否为同一个客户端使用
    private String sessionId;

    public ValidateCode() {
    }

    public ValidateCode(String code, LocalDateTime expireTime, String sessionId){
        this.code = code;
        this.expireTime = expireTime;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}
