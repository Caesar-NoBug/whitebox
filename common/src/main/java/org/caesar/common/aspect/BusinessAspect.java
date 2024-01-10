package org.caesar.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.Business;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * 业务切面，标记业务名称，便于标记异常信息和日志
 */
@Aspect
@Slf4j
@Component
public class BusinessAspect {

    @Around("execution(* org.caesar.controller.*Controller.*(..))")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("切面被调用了");

        String businessName = ((MethodSignature) joinPoint.getSignature())
                .getMethod().getName();

        ContextHolder.set(ContextHolder.BUSINESS_NAME, businessName);

        //TODO: 打印入参和响应结果的日志
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        //TODO: 控制日志粒度，把打印执行结果和入口参数变成可选项
        log.info("<{}>[执行结果]:{}", businessName, result.toString());

        long executeTime = System.currentTimeMillis() - startTime;

        if (executeTime > 2000)
            log.warn("业务执行时间过长：" + executeTime + "ms");

        return result;
    }

    //TODO: 加一个RPC切面【针对feignClient的方法接口】，统一添加日志
}
