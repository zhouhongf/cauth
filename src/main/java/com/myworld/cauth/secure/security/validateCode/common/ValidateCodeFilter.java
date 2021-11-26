package com.myworld.cauth.secure.security.validateCode.common;


import com.myworld.cauth.config.SecurityProperties;
import com.myworld.cauth.secure.security.validateCode.common.controller.ValidateCodeProcessorHolder;
import com.myworld.cauth.secure.security.validateCode.common.entity.ValidateCodeType;
import com.myworld.cauth.secure.security.validateCode.common.exception.ValidateCodeException;
import com.myworld.cauth.secure.security.validateCode.common.service.ValidateCodeProcessor;
import com.myworld.cauth.common.properties.SecurityConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2018/1/7 0007.
 */
@Component("validateCodeFilter")
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean{
    private static Logger log = LogManager.getRootLogger();
    /**
     * 验证码校验失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 系统配置信息
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 校验码处理器
     */
    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    /**
     * 存放所有需要检验验证码的url
     */
    private Map<String, ValidateCodeType> urlMap = new HashMap<>();

    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 初始化bean完成后再加载信息，也就是拦截的url
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        //urlMap中放入, 默认的登录url（"/doLogin"） 和 验证码为图片类型
        urlMap.put(SecurityConstants.DEFAULT_LOGIN_URL_FORM, ValidateCodeType.IMAGE);
        //通过addUrlToMap方法，将要拦截的url，多个url用逗号隔开，拆分成string[]数组后，全部放入urlMap中
        addUrlToMap(securityProperties.getCode().getImage().getUrl(), ValidateCodeType.IMAGE);

        //urlMap中放入, 默认的登录url（"/doSmsLogin"） 和 验证码为短信类型
        urlMap.put(SecurityConstants.DEFAULT_LOGIN_URL_MOBILE, ValidateCodeType.SMS);
        addUrlToMap(securityProperties.getCode().getSms().getUrl(), ValidateCodeType.SMS);
    }

    /**
     * 将系统中配置的需要校验验证码的URL，根据 校验类型 放入map
     */
    protected void addUrlToMap(String urlString, ValidateCodeType type) {
        if (StringUtils.isNotBlank(urlString)) {
            //根据“，”分割为String[]数组
            String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlString, ",");
            for (String url : urls) {
                urlMap.put(url, type);
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("【开始执行ValidateCodeFilter】");
        ValidateCodeType type = getValidateCodeType(request);
        if (type != null) {
            logger.info("校验请求(" + request.getRequestURI() + ")中的验证码,验证码类型" + type);
            try {
                ValidateCodeProcessor validateCodeProcessor = validateCodeProcessorHolder.findValidateCodeProcessor(type);
                logger.info("开始执行validateCodeProcessor.validate()方法来校验验证码");
                validateCodeProcessor.validate(new ServletWebRequest(request, response));
                logger.info("验证码校验通过");
            } catch (ValidateCodeException exception) {
                logger.info("执行validateCodeProcessor.validate()方法不成功");
                authenticationFailureHandler.onAuthenticationFailure(request, response, exception);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 获取校验码的类型，如果当前请求不需要校验，则返回null
     * request请求的方法不能是get
     */
    private ValidateCodeType getValidateCodeType(HttpServletRequest request) {
        ValidateCodeType result = null;
        if (!StringUtils.equalsIgnoreCase(request.getMethod(), "get")) {
            Set<String> urls = urlMap.keySet();
            for (String url : urls) {
                if (pathMatcher.match(url, request.getRequestURI())) {
                    result = urlMap.get(url);
                }
            }
        }
        return result;
    }
}
