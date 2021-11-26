package com.myworld.cauth.secure.security.redis;

import com.myworld.cauth.config.SecurityProperties;
import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.secure.security.validateCode.common.entity.SmsCode;
import com.myworld.cauth.secure.security.validateCode.common.entity.ValidateCode;
import com.myworld.cauth.secure.security.validateCode.common.entity.ValidateCodeType;
import com.myworld.cauth.secure.security.validateCode.common.exception.ValidateCodeException;
import com.myworld.cauth.common.model.ApiResultEnum;
import com.myworld.cauth.common.properties.SecurityConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.concurrent.TimeUnit;

@Component
public class ValidateCodeService {
    private static Logger log = LogManager.getRootLogger();
    public static final String HEADER_IMAGE_CODE = "imageCodeParam";

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate<String,Object> template;

    public void save(ServletWebRequest request, ValidateCode code, ValidateCodeType validateCodeType) {
        String theCodeType = validateCodeType.toString();
        log.info("theCodeType是{}", theCodeType);
        // String sessionId = request.getSessionId();
        // log.info("sessionId是{}", sessionId);
        // 因为是通过feignClient的okHttp传递进来的，所以request中的headers只有：accept值为:*/*， host值为:122.114.50.172:9999， connection值为:Keep-Alive， accept-encoding值为:gzip， user-agent值为:okhttp/3.8.1这些。
        // 所以只能通过url参数形式传递进来
        String imageCodeParam = request.getParameter("random");
        log.info("从request中获取到的imageCodeParam是：{}", imageCodeParam);

        String theKey;
        if (theCodeType.equals("SMS")) {
            //使用手机号作为key的一部分，将validateCode存入redis中，到时取得时候，也得凭借request请求中的手机号组装成key，来redis中取vaidateCode, 如果手机号不正确，那么key肯定不正确，那么就取不到validateCode
            theKey = theCodeType + SecurityConstants.VALIDATE_CODE + ((SmsCode) code).getMobile();
            log.info("【制作完成SMS验证码的theKey为：{}】", theKey);
        } else {
            // theKey = theCodeType + SecurityConstants.VALIDATE_CODE + sessionId;
            theKey = theCodeType + SecurityConstants.VALIDATE_CODE + imageCodeParam;
            log.info("【制作完成Image验证码的theKey为：{}】", theKey);
        }
        // 因为imageCodeProperties类继承于smsCodeProperties类，其expireIn属性是相同的，所以这里直接取smsCode的expireIn
        int expireIn = securityProperties.getCode().getSms().getExpireIn();
        template.opsForValue().set(theKey, code, expireIn, TimeUnit.SECONDS);
    }


    public ValidateCode get(ServletWebRequest request, ValidateCodeType validateCodeType) {
        String theCodeType = validateCodeType.toString();
        log.info("从redis中获取验证码，theCodeType是{}", theCodeType);
        String theKey;
        if (theCodeType.equals("SMS")) {
            //根据request中的另一个参数mobile，即手机号，来组装成一个redis的key来从redis中查询
            String usernameInRequest = request.getParameter(SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE);
            usernameInRequest = MyUserKeyService.getRealUsername(usernameInRequest);
            log.info("从ServletWebRequest的参数中获取手机号码usernameInRequest是{}", usernameInRequest);
            theKey = theCodeType + SecurityConstants.VALIDATE_CODE + usernameInRequest;
            log.info("【从redis中获取smsCode验证码，theKey是{}】", theKey);
        } else {
            // String sessionId = request.getSessionId();
            // log.info("从redis中获取验证码，sessionId是{}", sessionId);
            // theKey = theCodeType + SecurityConstants.VALIDATE_CODE + sessionId;

            String imageCodeParam = request.getRequest().getHeader(HEADER_IMAGE_CODE);
            log.info("从request中获取到的imageCodeParam是：{}", imageCodeParam);
            theKey = theCodeType + SecurityConstants.VALIDATE_CODE + imageCodeParam;
            log.info("【从redis中获取imageCode验证码，theKey是{}】", theKey);
        }
        ValidateCode validateCode = (ValidateCode) template.opsForValue().get(theKey);
        if(validateCode == null) {
            throw new ValidateCodeException(ApiResultEnum.CODE_ERROT.getCode(), "该验证码不存在");
        }
        return validateCode;
    }


    public void remove(ServletWebRequest request, ValidateCodeType validateCodeType) {
        String theCodeType = validateCodeType.toString();
        String theKey;
        if (theCodeType.equals("SMS")) {
            String usernameInRequest = request.getParameter(SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE);
            theKey = theCodeType + SecurityConstants.VALIDATE_CODE + usernameInRequest;
        } else {
            // String sessionId = request.getSessionId();
            // theKey = theCodeType + SecurityConstants.VALIDATE_CODE + sessionId;
            String imageCodeParam = request.getRequest().getHeader(HEADER_IMAGE_CODE);
            theKey = theCodeType + SecurityConstants.VALIDATE_CODE + imageCodeParam;
        }
        template.delete(theKey);
        //也可以使用下面的方法删除缓存
        //template.opsForValue().getOperations().delete(theKey);
    }

}
