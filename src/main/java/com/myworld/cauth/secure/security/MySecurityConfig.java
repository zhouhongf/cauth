package com.myworld.cauth.secure.security;

import com.myworld.cauth.secure.security.authorize.AuthorizeConfigProvider;
import com.myworld.cauth.secure.security.handler.MyLogoutSuccessHandler;
import com.myworld.cauth.secure.security.authenticate.JWT.JwtAuthenticationSecurityConfig;
import com.myworld.cauth.secure.security.authenticate.UsernamePasswordDecodeAuthenticationFilter;
import com.myworld.cauth.secure.security.authenticate.mobile.SmsCodeAuthenticationSecurityConfig;
import com.myworld.cauth.secure.security.validateCode.common.ValidateCodeSecurityConfig;
import com.myworld.cauth.common.properties.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthorizeConfigProvider authorizeConfigProvider;

    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Autowired
    private JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;

    @Autowired
    private AuthenticationSuccessHandler myLoginSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler myLoginFailureHandler;
    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    @Qualifier("myUserDetailService")
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UsernamePasswordDecodeAuthenticationFilter usernamePasswordDecodeAuthenticationFilter = new UsernamePasswordDecodeAuthenticationFilter();
        usernamePasswordDecodeAuthenticationFilter.setAuthenticationManager(authenticationManager());
        usernamePasswordDecodeAuthenticationFilter.setAuthenticationSuccessHandler(myLoginSuccessHandler);
        usernamePasswordDecodeAuthenticationFilter.setAuthenticationFailureHandler(myLoginFailureHandler);

        http
                .formLogin()//使用表单登录，不再使用默认httpBasic方式
                .loginPage(SecurityConstants.DEFAULT_LOGIN_PAGE)//如果请求的URL需要认证则跳转的URL"/login"页面
                .loginProcessingUrl(SecurityConstants.DEFAULT_LOGIN_URL_FORM)//处理表单中自定义的登录URL"/doLogin"
                .successHandler(myLoginSuccessHandler)//登录成功处理器，返回JSON
                .failureHandler(myLoginFailureHandler)//登录失败处理器
                .and()
                .apply(validateCodeSecurityConfig)//验证码拦截
                .and()
                .apply(smsCodeAuthenticationSecurityConfig)//短信认证拦截
                .and()
                .apply(jwtAuthenticationSecurityConfig)
                .and()
                .addFilterBefore(usernamePasswordDecodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .logout()
                .logoutUrl(SecurityConstants.DEFAULT_LOGOUT_URL)//默认退出地址为/doLogout
                //.logoutSuccessUrl(SecurityConstants.DEFAULT_LOGOUT_PAGE)//退出之后跳转到的页面/logout
                .logoutRequestMatcher(new AntPathRequestMatcher(SecurityConstants.DEFAULT_LOGOUT_URL))
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .and()
                .authorizeRequests().antMatchers(SecurityConstants.DEFAULT_LOGIN_ADMIN_PAGE,
                SecurityConstants.DEFAULT_LOGIN_PAGE,
                SecurityConstants.DEFAULT_LOGOUT_PAGE,
                SecurityConstants.DEFAULT_LOGIN_URL_FORM,
                SecurityConstants.DEFAULT_LOGIN_URL_MOBILE,
                SecurityConstants.DEFAULT_LOGOUT_URL,
                SecurityConstants.DEFAULT_REGISTER_URL,
                "/druid/**",
                "/validatecode/**",             //用于验证码
                "/checkAndRefreshJwtToken/**",  //用户刷新token
                "/checkJwtAndRefreshCurrentUser/**",
                "/checkValidateCode/**",

                "/users",
                "/avatar/**",
                "/getFileLocation/**",
                "/getBase64File/**",

                "/getTinymcePhotoLocation/**",
                "/getSmallPicLocation/**",
                "/downloadFile/**",

                "/getSiteInfo",
                "/getSiteInfoReleaseTime",

                "/validateToken/**",
                "/test/**",

                "/visitor/**",
                "/bank/**",

                "/**/*.js",
                "/**/*.json",
                "/**/*.jsonp",
                "/**/*.scss",
                "/**/*.css",
                "/**/*.jpg",
                "/**/*.jpeg",
                "/**/*.png",
                "/**/*.woff2",
                "/**/*.ttf",
                "/**/*.woff",
                "/**/*.svg",
                "/**/*.eot")
                .permitAll()//以上的请求都不需要认证
                .and()
                .cors()
                .and()
                .csrf().disable()//关闭csrd拦截
        ;
        //安全模块单独配置
        authorizeConfigProvider.config(http.authorizeRequests());
    }

    /**
     * 去除角色中role_的前缀
     * 表达式需要.access("hasRole('ADMIN')");
     */
    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        auth.eraseCredentials(false);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}

