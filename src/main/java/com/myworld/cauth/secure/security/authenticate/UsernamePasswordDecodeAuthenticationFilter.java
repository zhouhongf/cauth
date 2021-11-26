package com.myworld.cauth.secure.security.authenticate;

import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.secure.security.handler.MyLoginFailureHandler;
import com.myworld.cauth.secure.security.handler.MyLoginSuccessHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UsernamePasswordDecodeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static Logger log = LogManager.getRootLogger();
    private boolean postOnly = true;

    public UsernamePasswordDecodeAuthenticationFilter() {
        AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/doLogin", "POST");
        this.setRequiresAuthenticationRequestMatcher(requestMatcher);
        this.setAuthenticationManager(getAuthenticationManager());
        this.setAuthenticationSuccessHandler(new MyLoginSuccessHandler());
        this.setAuthenticationFailureHandler(new MyLoginFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("【开始执行UsernamePasswordDecodeAuthenticationFilter】");
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 从请求中获取用户名/密码，也就是用户填写在用户名/密码登录表单中的这些信息
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        // 配合前端，对username, password进行CryptoJS解密
        String realUsername = MyUserKeyService.getRealUsername(username);
        String realPassword = MyUserKeyService.getRealPassword(password);
        log.info("【真实的用户名是：{}】", realUsername);
        log.info("【真实的密码是：{}】", realPassword);

        // 根据用户提供的用户名/密码信息构建一个认证token
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(realUsername, realPassword);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        // 交给 authenticationManager执行真正的用户身份认证
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
