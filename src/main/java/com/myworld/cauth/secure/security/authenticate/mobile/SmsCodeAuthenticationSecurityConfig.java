package com.myworld.cauth.secure.security.authenticate.mobile;


import com.myworld.cauth.secure.security.handler.MyLoginSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 短信登录配置
 * Created on 2018/1/10.
 */
@Slf4j
@Component
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private AuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyLoginSuccessHandler myLoginSuccessHandler;

    @Autowired
    @Qualifier("myUserDetailService")
    private UserDetailsService userDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        log.info("启用SmsCodeAuthenticationSecurityConfig");
        //自定义SmsCodeAuthenticationFilter过滤器
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(myLoginSuccessHandler);

        log.info("启用SmsCodeAuthenticationProvider");
        //设置自定义SmsCodeAuthenticationProvider的认证器userDetailsService
        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

        log.info("设置smsCodeAuthenticationFilter在UsernamePasswordAuthenticationFilter过滤前执行");
        //在UsernamePasswordAuthenticationFilter过滤后执行
        http.authenticationProvider(smsCodeAuthenticationProvider).addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
