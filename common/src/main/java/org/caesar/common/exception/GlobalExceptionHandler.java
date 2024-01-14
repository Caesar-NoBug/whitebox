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

    public static final String MESSAGE_FORMAT = "(%s) [FAIL] '%s': {%s}";

    public static final String MSG_BUSINESS_EXCEPTION = "(Business Exception)";
    public static final String MSG_INVALID_REQUEST_PARAM = "(Invalid Request Parameter)";
    public static final String MSG_SYSTEM_ERROR = "(Unexpected System Error)";

    //TODO: filter获取信息
    @ExceptionHandler(BusinessException.class)
    public Response<Void> businessExceptionHandler(BusinessException e) {

        ErrorCode code = e.getCode();

        String message = String.format(
                MESSAGE_FORMAT,
                code.getMessage(),
                ContextHolder.get(ContextHolder.BUSINESS_NAME),
                e.getMessage());

        //TODO: 控制日志级别
        if (!ErrorCode.SYSTEM_ERROR.equals(code)) {
            LogUtil.warn(message, e);
        } else {
            LogUtil.error(message, e);
        }

        return Response.error(e.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Void> constraintViolationExceptionHandler(ConstraintViolationException e) {

        String message = String.format(
                MESSAGE_FORMAT,
                MSG_INVALID_REQUEST_PARAM,
                ContextHolder.get(ContextHolder.BUSINESS_NAME),
                e.getMessage());

        LogUtil.warn(message, e);

        return Response.error(ErrorCode.ILLEGAL_PARAM_ERROR, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public Response<Void> runtimeExceptionHandler(RuntimeException e) {
        //异常信息输出到日志中，不能直接返回给客户端
        String message = String.format(
                MESSAGE_FORMAT,
                MSG_SYSTEM_ERROR,
                ContextHolder.get(ContextHolder.BUSINESS_NAME),
                e.getMessage());

        ErrorCode error = ErrorCode.SYSTEM_ERROR;

        return Response.error(error.getCode(), error.getMessage());
    }

}
