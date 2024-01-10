package org.caesar.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.common.enums.ErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String MESSAGE_FORMAT = "[%s失败]: {%s}";

    //TODO: filter获取信息，
    @ExceptionHandler(BusinessException.class)
    public Response<Void> businessExceptionHandler(BusinessException e) {
        String message = String.format(
                MESSAGE_FORMAT,
                ContextHolder.get(ContextHolder.BUSINESS_NAME),
                e.getMessage());

        //TODO: 控制日志级别
        LogUtil.error("业务异常：", e);

        return Response.error(e.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Void> constraintViolationExceptionHandler(ConstraintViolationException e) {

        String message = String.format(
                MESSAGE_FORMAT,
                ContextHolder.get(ContextHolder.BUSINESS_NAME),
                e.getMessage());

        LogUtil.warn("参数校验异常：", e);

        return Response.error(ErrorCode.ILLEGAL_PARAM_ERROR, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public Response<Void> runtimeExceptionHandler(RuntimeException e) {
        //异常信息输出到日志中，不能直接返回给客户端
        LogUtil.error("系统异常：", e);
        ErrorCode error = ErrorCode.SYSTEM_ERROR;
        return Response.error(error.getCode(), error.getMessage());
    }

}
