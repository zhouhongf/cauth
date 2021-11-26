package com.myworld.cauth.secure.security.authenticate.JWT;


import com.myworld.cauth.secure.security.validateCode.common.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;

@Component("jwtAuthenticationSecurityConfig")
public class JwtAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private Filter jwtAuthenticationFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(jwtAuthenticationFilter, ValidateCodeFilter.class);
    }
}
