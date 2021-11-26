package com.myworld.cauth.config;

import com.myworld.cauth.common.util.JwtUtil;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FeignOkHttpInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpServletRequest request = this.getHttpServletRequest();
        if (request != null){
            String authorizationValue = request.getHeader(JwtUtil.HEADER_AUTH);
            if (authorizationValue != null) {
                Request requestWithAuthorization = originalRequest.newBuilder().addHeader(JwtUtil.HEADER_AUTH, authorizationValue).build();
                return chain.proceed(requestWithAuthorization);
            }
        }
        return chain.proceed(originalRequest);
    }

    private HttpServletRequest getHttpServletRequest() {
        try{
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        } catch (Exception e){
            return null;
        }
    }
}
