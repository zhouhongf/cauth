package com.myworld.cauth.secure.security.validateCode.sms;


import com.myworld.cauth.secure.security.MyUserKeyService;
import com.myworld.cauth.secure.security.validateCode.common.entity.ValidateCode;
import com.myworld.cauth.secure.security.validateCode.common.service.impl.AbstractValidateCodeProcessor;
import com.myworld.cauth.common.properties.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Created on 2018/1/10.
 */
@Component("smsValidateCodeProcessor")
public class SmsCodeProcessor extends AbstractValidateCodeProcessor<ValidateCode> {

    /**
     * 短信验证码发送器
     */
    @Autowired
    private SmsCodeSender smsCodeSender;


    @Override
    protected void send(ServletWebRequest request, ValidateCode validateCode) throws Exception {
        //DEFAULT_PARAMETER_NAME_MOBILE = "mobile"。
        String paramName = SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE;
        //从ServletWebRequest中获得手机号码。
        String mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(), paramName);
        mobile = MyUserKeyService.getRealUsername(mobile);
        //使用SmsCodeSender向mobile发送验证码
        smsCodeSender.send(mobile, validateCode.getCode());
    }

}
