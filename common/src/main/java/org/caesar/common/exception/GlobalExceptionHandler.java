package org.caesar.common.exception;

import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.common.enums.ErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String RESP_MSG_FORMAT = "%s: {%s}";

    @ExceptionHandler(BusinessException.class)
    public<T> Response<T> businessExceptionHandler(BusinessException e) {

        ErrorCode code = e.getCode();

        if (!ErrorCode.SYSTEM_ERROR.equals(code)) {
            LogUtil.warn(code, e.getMessage());
        } else {
            LogUtil.error(code, e.getMessage(), e);
        }

        return Response.error(code, toResponseMessage(code, e.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public <T> Response<T> constraintViolationExceptionHandler(ConstraintViolationException e) {

        ErrorCode code = ErrorCode.INVALID_ARGS_ERROR;

        LogUtil.warn(code, e.getMessage());

        return Response.error(code, toResponseMessage(code, e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public <T> Response<T> runtimeExceptionHandler(RuntimeException e) {

        ErrorCode code = ErrorCode.SYSTEM_ERROR;
        LogUtil.error(code, e.getMessage(), e);

        return Response.error(code, toResponseMessage(code, ""));
    }

    private String toResponseMessage(ErrorCode code, String detailMessage) {
        // 为安全考虑，系统错误的详细原因不返回给前端
        if(!ErrorCode.SYSTEM_ERROR.equals(code))
            return String.format(RESP_MSG_FORMAT, code.getMessage(), detailMessage);
        else
            return code.getMessage();
    }

}
