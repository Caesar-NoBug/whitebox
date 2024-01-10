package org.caesar.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.log.LogUtil;
import org.caesar.common.log.MethodLogger;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.enums.LogType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class LogAspect {

    // 缓存方法参数名称
    private final Map<Method, List<String>> CACHE_PARAM_NAMES = new ConcurrentHashMap<>();

    @Around("@annotation(org.caesar.common.log.MethodLogger)")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        MethodLogger logger = method.getAnnotation(MethodLogger.class);

        if (logger.args()) logParam(method, joinPoint.getArgs());

        Object result;

        String prevName = ContextHolder.getBusinessName();

        ContextHolder.setBusinessName(logger.value());

        if (logger.time()) {
            result = joinPoint.proceed();
        } else {
            long start = System.currentTimeMillis();
            result = joinPoint.proceed();
            logTime(System.currentTimeMillis() - start);
        }

        // 恢复接口名称
        ContextHolder.setBusinessName(prevName);

        if (logger.result()) logResult(result);

        return result;
    }

    private void logParam(Method method, Object[] args) {

        StringBuilder sb = new StringBuilder();

        int i = 0;

        try {
            for (String name : getParamNames(method)) {
                sb.append(name).append(':').append(args[i++]);
            }
        } catch (IndexOutOfBoundsException e) {
            // 参数个数不匹配
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "illegal arg count or param name count");
        }

        LogUtil.info(LogType.METHOD_ARGS, sb.toString());
    }

    private void logResult(Object result) {
        LogUtil.info(LogType.METHOD_RESULT, String.valueOf(result));
    }

    private void logTime(long time) {
        LogUtil.info(LogType.METHOD_EXECUTE_TIME, String.valueOf(time));
    }

    private List<String> getParamNames(Method method) {

        List<String> paramNames = this.CACHE_PARAM_NAMES.get(method);

        if (Objects.isNull(paramNames)) {

            paramNames = new ArrayList<>();

            for (Parameter parameter : method.getParameters()) {
                paramNames.add(parameter.getName());
            }

            this.CACHE_PARAM_NAMES.put(method, paramNames);
        }

        return paramNames;
    }

}
