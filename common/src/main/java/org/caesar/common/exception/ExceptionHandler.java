package org.caesar.common.exception;

import org.caesar.domain.common.vo.Response;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;
import java.util.function.Supplier;

@Component
public class ExceptionHandler {

    @Resource
    private GlobalExceptionHandler globalExceptionHandler;

    /**
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


}
