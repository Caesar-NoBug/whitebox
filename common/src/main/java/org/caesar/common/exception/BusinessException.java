package org.caesar.common.exception;

import lombok.Getter;
import org.caesar.domain.common.enums.ErrorCode;

@Getter
// 业务异常
public class BusinessException extends RuntimeException{

    private final int code;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

}

