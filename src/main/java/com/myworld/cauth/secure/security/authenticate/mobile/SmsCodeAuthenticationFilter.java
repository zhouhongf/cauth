package com.myworld.cauth.secure.security.authenticate.mobile;

import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.common.properties.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 短信登录过滤器
 */
@Slf4j
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * request中必须含有mobile参数
     */
    private String mobileParameter = SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE;
    /**
     * post请求
     */
    private boolean postOnly = true;

    protected SmsCodeAuthenticationFilter() {
        // 处理的手机验证码登录请求处理url
        super(new AntPathRequestMatcher(SecurityConstants.DEFAULT_LOGIN_URL_MOBILE, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("【开始执行SmsCodeAuthenticationFilter】");
        //判断是不是post请求
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        log.info("已完成请求是否是POST的判断");
        //从请求中获取手机号码
        String mobile = obtainMobile(request);
        if (mobile == null) {
            mobile = "";
        }

        // 配合前端，对mobile进行CryptoJS解密
        String realUsername = MyUserKeyService.getRealUsername(mobile);
        log.info("从request中提取的mobile数值为" + realUsername);
        //创建SmsCodeAuthenticationToken(未认证)
        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(realUsername);

        //设置用户信息
        setDetails(request, authRequest);
        //返回Authentication实例
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * 获取手机号
     */
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public void setMobileParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.mobileParameter = usernameParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getMobileParameter() {
        return mobileParameter;
    }
}
