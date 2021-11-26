package com.myworld.cauth.secure.security.handler;

import com.myworld.cauth.secure.security.validateCode.common.exception.ValidateCodeException;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.util.ResultUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created on 2017/11/7.
 */
@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResult handle(Exception e) {
        if (e instanceof ValidateCodeException) {
            ValidateCodeException validateCodeException = (ValidateCodeException) e;
            return ResultUtil.error(validateCodeException.getCode(), validateCodeException.getMessage());
        } else {
            return ResultUtil.error(-1, "未知错误");
        }
    }

}
