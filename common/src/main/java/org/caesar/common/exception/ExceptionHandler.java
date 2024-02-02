package org.caesar.common.exception;

import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class ExceptionHandler {

    @Resource
    private GlobalExceptionHandler globalExceptionHandler;

    /**
     * 用于处理controller方法外的异常，并且返回封装好的结果
     * @param supplier 实际运行的方法
     * @param <T>      返回值类型
     * @return 方法的返回值（当发生异常时则转换成对应的Response对象）
     */
    public <T> Response<T> handleException(Supplier<Response<T>> supplier) {
        Response<T> result;

        try {
            result = supplier.get();
        } catch (BusinessException e) {
            return globalExceptionHandler.businessExceptionHandler(e);
        } catch (ConstraintViolationException e) {
            return globalExceptionHandler.constraintViolationExceptionHandler(e);
        } catch (RuntimeException e) {
            return globalExceptionHandler.runtimeExceptionHandler(e);
        }

        return result;
    }

    public void handleException(Throwable e) {
        if(e instanceof BusinessException) {
            ErrorCode code = ((BusinessException) e).getCode();
            //TODO: 控制日志级别
            if (!ErrorCode.SYSTEM_ERROR.equals(code)) {
                LogUtil.warn(code, e.getMessage());
            } else {
                LogUtil.error(code, e.getMessage(), e);
            }
        } else if(e instanceof ConstraintViolationException) {
            ErrorCode code = ErrorCode.INVALID_ARGS_ERROR;
            LogUtil.warn(code, e.getMessage());
        } else if(e instanceof RuntimeException) {
            ErrorCode code = ErrorCode.SYSTEM_ERROR;
            LogUtil.error(code, e.getMessage(), e);
        }

    }

}
