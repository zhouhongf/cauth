package com.myworld.cauth.secure.security.authenticate.mobile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 短信登录验证信息封装类
 * Created on 2018/1/10.
 */
@Slf4j
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    private final Object principal;

    /**
     * SmsCodeAuthenticationFilter中构建的未认证的Authentication
     */
    public SmsCodeAuthenticationToken(String mobile) {
        super(null);
        this.principal = mobile;
        setAuthenticated(false);
        log.info("SmsCodeAuthenticationToken构造函数实例化，参数就为mobile，设置principle=手机号");
    }

    /**
     * SmsCodeAuthenticationProvider中构建已认证的Authentication
     */
    public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); // must use super, as we override
        log.info("SmsCodeAuthenticationToken构造函数实例化，参数为principal和authorities");
    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * @param isAuthenticated
     * @throws IllegalArgumentException
     */
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
