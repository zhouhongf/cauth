package com.myworld.cauth.secure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myworld.cauth.common.util.ResultUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义登录失败处理
 */
@Component(value = "myLoginFailureHandler")
public class MyLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // super.onAuthenticationFailure(request, response, exception);
        // response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());//服务器内部异常
        String message;
        if ("Bad credentials".equals(exception.getMessage())) {
            message = "用户名或密码错误！";
        } else {
            message = exception.getMessage();
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ResultUtil.error(-2, message)));
    }
}
