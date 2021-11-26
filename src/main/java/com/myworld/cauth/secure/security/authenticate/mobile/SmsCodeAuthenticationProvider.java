package com.myworld.cauth.secure.security.authenticate.mobile;

import com.myworld.cauth.secure.data.entity.SysAdmin;
import com.myworld.cauth.secure.data.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 短信登录验证逻辑
 */
@Slf4j
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    /**
     * 只有Authentication为SmsCodeAuthenticationToken使用此Provider认证
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("启用SmsCodeAuthenticationProvider");
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        //调用自定义的userDetailsService认证
        UserDetails user = userDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());

        if (user == null || StringUtils.isEmpty(user.getUsername())) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }
        //如果user不为空,则重新构建SmsCodeAuthenticationToken（已认证）
        SmsCodeAuthenticationToken authenticationTokenNew = new SmsCodeAuthenticationToken(user, user.getAuthorities());
        authenticationTokenNew.setDetails(authenticationToken.getDetails());

        // ！！！以下内容生产环境下，都可以删除
        log.info("【登录成功】【SmsCodeAuthenticationProvider】的authenticate()方法中的认证结果authenticationTokenNew为" + authenticationTokenNew);
        Object userDetails = authenticationTokenNew.getPrincipal();
        String className = userDetails.getClass().getSimpleName();
        if(className.equals("SysUser")) {
            SysUser sysUser = (SysUser) userDetails;
            log.info("【登录用户：" + sysUser.getUsername() + "】" );
            log.info("【用户Id】：" + sysUser.getId());
        } else if (className.equals("SysAdmin")) {
            SysAdmin sysAdmin = (SysAdmin) userDetails;
            log.info("【登录用户：" + sysAdmin.getUsername() + "】" );
            log.info("【用户Id】：" + sysAdmin.getId());
        }


        return authenticationTokenNew;
    }


    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
