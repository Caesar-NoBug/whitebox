package org.caesar.common.exception;

import lombok.Getter;
import org.caesar.domain.common.enums.ErrorCode;

@Getter
// 业务异常
public class BusinessException extends RuntimeException{

    private final ErrorCode code;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }
}

