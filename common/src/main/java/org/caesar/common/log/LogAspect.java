package org.caesar.common.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.util.MethodUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.enums.LogType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    @Around("@annotation(org.caesar.common.log.Logger)")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        Logger logger = method.getAnnotation(Logger.class);

        ContextHolder.setBusinessName(logger.value());

        if (logger.args()) logParam(method, joinPoint.getArgs());

        Object result = null;

        String prevName = ContextHolder.getBusinessName();

        try {
            if (logger.time()) {
                result = joinPoint.proceed();
            } else {
                result = proceedAndLogTime(joinPoint);
            }
        } finally {
            // 恢复接口名称
            ContextHolder.setBusinessName(prevName);
            if (logger.result()) logResult(result);
        }

        return result;
    }

    private void logParam(Method method, Object[] args) {

        try {
            int i = 0;
            StringBuilder sb = new StringBuilder();

            for (String name : MethodUtil.getParamNames(method)) {
                sb.append(name).append(':').append(args[i++]);
            }

            LogUtil.info(LogType.METHOD_ARGS, sb.toString());
        } catch (Throwable e) {
            // 只警告不抛出异常避免影响业务执行
            LogUtil.warn(ErrorCode.SYSTEM_ERROR, "Fail to log the args of method:" + method, e);
        }

    }

    private void logResult(Object result) {
        LogUtil.info(LogType.METHOD_RESULT, String.valueOf(result));
    }

    private Object proceedAndLogTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            LogUtil.info(LogType.METHOD_EXECUTE_TIME, System.currentTimeMillis() - start + "ms.");
        }

        return result;
    }

}
