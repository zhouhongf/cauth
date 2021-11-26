package com.myworld.cauth.secure.security.validateCode.sms;


import com.myworld.cauth.config.SecurityProperties;
import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.secure.security.validateCode.common.entity.SmsCode;
import com.myworld.cauth.secure.security.validateCode.common.exception.ValidateCodeException;
import com.myworld.cauth.secure.security.validateCode.common.service.ValidateCodeGenerator;
import com.myworld.cauth.common.properties.SecurityConstants;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created on 2018/1/10.
 * SMS验证码生成器
 */
@Component("smsValidateCodeGenerator")
public class SmsCodeGenerator implements ValidateCodeGenerator {
    private static Logger log = LogManager.getRootLogger();
    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate<String,Object> template;

    /**
     * 验证码放入redis时的key前缀
     */
    private String VALIDATE_CODE = SecurityConstants.VALIDATE_CODE;

    @Override
    public SmsCode generate(ServletWebRequest request) {
        // 从ServletWebRequest中获得手机号码, DEFAULT_PARAMETER_NAME_MOBILE = "mobile"。
        String mobile = request.getParameter(SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE);
        // 解密mobile
        mobile = MyUserKeyService.getRealUsername(mobile);
        String theKeyCheck = "SMS" + VALIDATE_CODE + mobile;

        if (template.hasKey(theKeyCheck)){
            SmsCode oldSmsCode = (SmsCode)template.opsForValue().get(theKeyCheck);
            LocalDateTime oldExpireTime = oldSmsCode.getExpireTime();
            ZoneId zoneId = ZoneId.systemDefault();
            Instant instant = oldExpireTime.atZone(zoneId).toInstant();
            long expireTime = instant.toEpochMilli();
            log.info("expireTime是：{}", expireTime);
            long currentTime = System.currentTimeMillis();
            log.info("currentTime是：{}", currentTime);
            long diffTime = expireTime - currentTime;
            int expireIn = securityProperties.getCode().getSms().getExpireIn() * 1000;
            log.info("expireIn是：{}", expireIn);
            if (diffTime > 0 && diffTime < expireIn) {
                throw new ValidateCodeException("请勿重复请求验证码");
            }
        }

        // 根据 securityProperties及其子类，获得需要生成的验证码的长度，生成一串随机数字。
        String code = RandomStringUtils.randomNumeric(securityProperties.getCode().getSms().getLength());
        // 从ServletWebRequest中获得sessionId。
        String theSessionId = request.getSessionId();
        int expireIn = securityProperties.getCode().getImage().getExpireIn();
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(expireIn);
        return new SmsCode(mobile, code, expireTime, theSessionId);
    }
}
