package com.myworld.cauth.secure.security.validateCode.common.service.impl;


import com.myworld.cauth.secure.security.redis.ValidateCodeService;
import com.myworld.cauth.secure.security.validateCode.common.entity.SmsCode;
import com.myworld.cauth.secure.security.validateCode.common.entity.ValidateCode;
import com.myworld.cauth.secure.security.validateCode.common.entity.ValidateCodeType;
import com.myworld.cauth.secure.security.validateCode.common.exception.ValidateCodeException;
import com.myworld.cauth.secure.security.validateCode.common.service.ValidateCodeGenerator;
import com.myworld.cauth.secure.security.validateCode.common.service.ValidateCodeProcessor;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.model.ApiResultEnum;
import com.myworld.cauth.common.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.Map;

public abstract class AbstractValidateCodeProcessor<C extends ValidateCode> implements ValidateCodeProcessor {
    private static Logger log = LogManager.getRootLogger();
    @Autowired
    private Map<String, ValidateCodeGenerator> validateCodeGenerators;

    @Autowired
    private ValidateCodeService validateCodeService;


    /**
     * 根据Holder返回的CodeProcessor类型来获取验证码的类型
     */
    private ValidateCodeType getValidateCodeType(ServletWebRequest request) {
        // 从ImageCodeProcessor或者SmsCodeProcessor类名中，提取出Image或者Sms
        String type = StringUtils.substringBefore(getClass().getSimpleName(), "CodeProcessor");
        // 通过大写的验证码类型IMAGE或SMS，返回IMAGE或SMS
        return ValidateCodeType.valueOf(type.toUpperCase());
    }


    /**
     * 验证码关键步骤
     * 验证码产生、保存、发送
     */
    @Override
    public void create(ServletWebRequest request) throws Exception{
        // 第一步，使用generate()方法，参数是ServletWebRequest，生成验证码
        C validateCode = this.generate(request);
        // 第二步， 保存 ServletWebRequest和验证码
        this.save(request, validateCode);
        // 第三步， 发送 ServletWebRequest和验证码
        this.send(request, validateCode);
    }


    /**
     * 生成验证码
     */
    private C generate(ServletWebRequest request) {
        String type = this.getValidateCodeType(request).toString().toLowerCase();
        log.info("生成的验证码的类型为{}", type);
        String generatorName = type + ValidateCodeGenerator.class.getSimpleName();
        log.info("拼接出来的generatorName为{}", generatorName);
        ValidateCodeGenerator validateCodeGenerator = validateCodeGenerators.get(generatorName);
        if (validateCodeGenerator == null) {
            throw new ValidateCodeException("验证码生成器" + generatorName + "不存在");
        }
        return (C) validateCodeGenerator.generate(request);
    }

    /**
     * 保存验证码
     * 保存在redis当中
     */
    private void save(ServletWebRequest request,C validateCode){
        String theCode = validateCode.getCode();
        LocalDateTime theExpireTime = validateCode.getExpireTime();
        String theSessionId = validateCode.getSessionId();

        String type = this.getValidateCodeType(request).toString().toLowerCase();
        if (type.equals("sms")) {
            SmsCode smsCode = (SmsCode)validateCode;
            SmsCode code = new SmsCode(smsCode.getMobile(), theCode, theExpireTime, theSessionId);
            log.info("ValidateCode code = new SmsCode()方法: 手机号" + smsCode.getMobile() + ", 验证码数字" + theCode + ", 到期时间" + theExpireTime);
            validateCodeService.save(request, code, this.getValidateCodeType(request));
            log.info("validateCodeRedisService.save()方法已执行完毕");
        } else {
            ValidateCode code = new ValidateCode(theCode, theExpireTime, theSessionId);
            validateCodeService.save(request, code, this.getValidateCodeType(request));
        }
    }

    /**
     * 发送校验码，由子类实现
     */
    protected abstract void send(ServletWebRequest request, C validateCode) throws Exception;


    /**
     * 验证 验证码
     */
    @SuppressWarnings("unchecked")
    @Override
    public ApiResult validate(ServletWebRequest request){
        //根据CodeProcessor类名的前缀，Image或Sms，来判别validateCode的Type
        ValidateCodeType codeType = this.getValidateCodeType(request);
        log.info("查询到的验证码类型为："+ codeType);
        //从redis中取出保存的validateCode
        C codeInSave = (C) validateCodeService.get(request, codeType);
        //！！！！以下这个if条件，考虑一下，是否可以不用，因为去redis中根据key获取smsCode，凭借的也是本次request请求中的username参数，即手机号，如果手机号码不一致，那么凭借这个key是获取不到value的
        //if (codeType.getParamNameOnValidate().equals("smsCode")) {
        //    log.info("短信验证码，故开始验证是否是同一个手机号码");
        //    SmsCode smsCode = (SmsCode) codeInSave;
        //    String usernameInSave = smsCode.getMobile();
        //    String usernameInRequest = request.getParameter(SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE);
        //    if (!usernameInRequest.equals(usernameInSave)) {
        //        log.info("手机号码不匹配");
        //        throw new ValidateCodeException(ResultEnum.CODE_ERROT.getCode(),codeType + "手机号码不匹配");
        //    }
        //}
        return this.doValidate(request, codeType, codeInSave);
    }


    public ApiResult doValidate(ServletWebRequest request, ValidateCodeType codeType, ValidateCode codeInSave) {
        log.info("codeType是" + codeType.getParamNameOnValidate());
        //从request中取出String类型的验证码
        String codeInRequest = request.getParameter(codeType.getParamNameOnValidate());
        log.info("查询到的codeInRequest为："+ codeInRequest);

        if (StringUtils.isBlank(codeInRequest)) {
            log.info("验证码的值不能为空");
            throw new ValidateCodeException(ApiResultEnum.CODE_ERROT.getCode(),codeType + "验证码的值不能为空");
        }

        if (codeInSave == null) {
            log.info("验证码不存在");
            throw new ValidateCodeException(ApiResultEnum.CODE_ERROT.getCode(),codeType + "验证码不存在");
        }

        LocalDateTime theDataTime = codeInSave.getExpireTime();
        if (LocalDateTime.now().isAfter(theDataTime)) {
            log.info("验证码已过期");
            validateCodeService.remove(request, codeType);
            throw new ValidateCodeException(ApiResultEnum.CODE_ERROT.getCode(),codeType + "验证码已过期");
        }

        if (!StringUtils.equals(codeInSave.getCode(), codeInRequest)) {
            log.info("验证码不匹配");
            throw new ValidateCodeException(ApiResultEnum.CODE_ERROT.getCode(),codeType + "验证码不匹配");
        }

        log.info("【验证码验证通过】");
        validateCodeService.remove(request, codeType);
        return ResultUtil.success("验证码验证通过！");
    }
}
