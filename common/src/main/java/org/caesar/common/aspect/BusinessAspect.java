package org.caesar.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.Business;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * 业务切面，标记业务名称，便于标记异常信息和日志
 */
@Aspect
@Slf4j
public class BusinessAspect {

    @Around("execution(* *.*Controller.*(..))")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Business business = method.getAnnotation(Business.class);

        ContextHolder.set(ContextHolder.BUSINESS_NAME,
                Objects.isNull(business) ? "业务" : business.name());

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executeTime = System.currentTimeMillis() - startTime;

        //TODO: 清空ThreadLocal，避免线程污染
        ContextHolder.clear();

        if (executeTime > 2000)
            log.error("业务执行时间过长：" + executeTime + "ms");

        return result;
    }


}
