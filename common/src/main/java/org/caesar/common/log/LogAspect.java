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

        String prevName = ContextHolder.getBusinessName();

        ContextHolder.setBusinessName(logger.value());

        boolean logArgs = logger.args();

        if (logArgs) logArguments(method, joinPoint.getArgs());

        Object result = null;

        try {
            if (logger.time()) {
                result = proceedAndLogTime(joinPoint);
            } else {
                result = joinPoint.proceed();
            }
        } catch (Throwable e) {
            // 出现异常时打印导致错误的方法参数
            if(!logArgs) logArguments(method, joinPoint.getArgs());
            throw e;
        }
        finally {

            if (logger.result()) logResult(result);

            // 恢复接口名称
            ContextHolder.setBusinessName(prevName);
        }

        return result;
    }

    private void logArguments(Method method, Object[] args) {

        try {
            int i = 0;
            StringBuilder sb = new StringBuilder();

            for (String name : MethodUtil.getParamNames(method)) {
                sb.append(name).append(": ").append(args[i++]).append(", ");
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
