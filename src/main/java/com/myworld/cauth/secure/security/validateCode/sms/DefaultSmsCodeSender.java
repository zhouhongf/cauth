package com.myworld.cauth.secure.security.validateCode.sms;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created on 2018/1/10.
 */
public class DefaultSmsCodeSender implements SmsCodeSender {
    private static Logger log = LogManager.getRootLogger();
    @Override
    public void send(String mobile, String code) {
        log.warn("请配置真实的短信验证码发送器(SmsCodeSender)");
        log.info("向手机" + mobile + "发送短信验证码" + code);

        //以下代码，正式运行时，再使用。
        /**
        AppConfig config = ConfigLoader.load(ConfigLoader.ConfigType.Message);
        MESSAGEXsend submail = new MESSAGEXsend(config);
        submail.addTo(mobile);
        submail.setProject("RUCsa1");
        submail.addVar("code", code);
        submail.xsend();
        */
    }



}
