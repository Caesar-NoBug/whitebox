package org.caesar.common.exception;

import org.caesar.domain.constant.enums.ErrorCode;

//参数校验异常
public class ValidationException extends BusinessException{

    public ValidationException(String message) {
        super(ErrorCode.ILLEGAL_PARAM_ERROR, message);
    }

}
