package com.myworld.cauth.config;

import com.myworld.cauth.common.model.LoginType;
import com.myworld.cauth.common.properties.ValidateCodeProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "my.security")
public class SecurityProperties {

    private String signOutUrl = "/introduction";

    private LoginType loginType = LoginType.JSON;

    /**
     * 验证码配置
     */
    private ValidateCodeProperties code = new ValidateCodeProperties();

    /**
     * jwtAccessToken有效时间 1个小时
     */
    private int accessTokenValidSeconds = 60 * 60;
    /**
     * jwtRefreshToken有效时间 1天
     */
    private int refreshTokenValidSeconds = 60 * 60 * 24;
    /**
     * jwtAccessToken续签 宽限期 10钟
     */
    private int accessTokenGraceSeconds = 60 * 50;
    /**
     * jwtRefreshToken续签 宽限期 2个小时
     */
    private int refreshTokenGraceSeconds = 60 * 60 * 2;

    /**
     * 设置localStorage中currentUser有效时间（1小时）
     */
    private int validMills = 1000 * 60 * 60;


    /**
     * 默认的redis缓存时间为30分钟
     */
    private long defaultCacheMinute = 30L;
    /**
     * 内部消息缓存时间30分钟
     */
    private long internalMessageCacheMinute = 30L;

    public SecurityProperties() {
    }

    public String getSignOutUrl() {
        return signOutUrl;
    }

    public void setSignOutUrl(String signOutUrl) {
        this.signOutUrl = signOutUrl;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public ValidateCodeProperties getCode() {
        return code;
    }

    public void setCode(ValidateCodeProperties code) {
        this.code = code;
    }

    public int getAccessTokenValidSeconds() {
        return accessTokenValidSeconds;
    }

    public void setAccessTokenValidSeconds(int accessTokenValidSeconds) {
        this.accessTokenValidSeconds = accessTokenValidSeconds;
    }

    public int getRefreshTokenValidSeconds() {
        return refreshTokenValidSeconds;
    }

    public void setRefreshTokenValidSeconds(int refreshTokenValidSeconds) {
        this.refreshTokenValidSeconds = refreshTokenValidSeconds;
    }

    public int getAccessTokenGraceSeconds() {
        return accessTokenGraceSeconds;
    }

    public void setAccessTokenGraceSeconds(int accessTokenGraceSeconds) {
        this.accessTokenGraceSeconds = accessTokenGraceSeconds;
    }

    public int getRefreshTokenGraceSeconds() {
        return refreshTokenGraceSeconds;
    }

    public void setRefreshTokenGraceSeconds(int refreshTokenGraceSeconds) {
        this.refreshTokenGraceSeconds = refreshTokenGraceSeconds;
    }

    public int getValidMills() {
        return validMills;
    }

    public void setValidMills(int validMills) {
        this.validMills = validMills;
    }

    public long getDefaultCacheMinute() {
        return defaultCacheMinute;
    }

    public void setDefaultCacheMinute(long defaultCacheMinute) {
        this.defaultCacheMinute = defaultCacheMinute;
    }

    public long getInternalMessageCacheMinute() {
        return internalMessageCacheMinute;
    }

    public void setInternalMessageCacheMinute(long internalMessageCacheMinute) {
        this.internalMessageCacheMinute = internalMessageCacheMinute;
    }
}
