package org.caesar.common.exception;

import org.caesar.common.context.ContextHolder;
import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.common.enums.ErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String RESP_MSG_FORMAT = "%s: {%s}";

    //TODO: filter获取信息
    @ExceptionHandler(BusinessException.class)
    public Response<Void> businessExceptionHandler(BusinessException e) {

        ErrorCode code = e.getCode();

        //TODO: 控制日志级别
        if (!ErrorCode.SYSTEM_ERROR.equals(code)) {
            LogUtil.warn(code, e.getMessage());
        } else {
            LogUtil.error(code, e.getMessage(), e);
        }

        return Response.error(code, toResponseMessage(code, e.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Void> constraintViolationExceptionHandler(ConstraintViolationException e) {

        ErrorCode code = ErrorCode.ILLEGAL_PARAM_ERROR;

        LogUtil.warn(code, e.getMessage());

        return Response.error(code, toResponseMessage(code, e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public Response<Void> runtimeExceptionHandler(RuntimeException e) {

        ErrorCode code = ErrorCode.SYSTEM_ERROR;
        LogUtil.error(code, e.getMessage(), e);

        return Response.error(code, toResponseMessage(code, ""));
    }

    private String toResponseMessage(ErrorCode code, String detailMessage) {
        return String.format(RESP_MSG_FORMAT, code.getMessage(), detailMessage);
    }

}
