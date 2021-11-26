package com.myworld.cauth.secure.security.validateCode.common.exception;

import com.myworld.cauth.common.model.ApiResultEnum;
import org.springframework.security.core.AuthenticationException;


public class ValidateCodeException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    public ValidateCodeException(ApiResultEnum apiResultEnum) {
        super(apiResultEnum.getMsg());
        this.code = apiResultEnum.getCode();
    }

    public ValidateCodeException(String msg) {
        super(msg);
    }

    public ValidateCodeException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
