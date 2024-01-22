package org.caesar.common.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Idempotent {

    // 业务名称(用作唯一前缀)
    String value() default "";

    // 指定请求唯一标识变量
    String reqId() default "";

    // 唯一标识数据类型
    Class<?> idType() default String.class;

    // 过期时间(单位:秒)【保证在expire期间内接收的请求不会重复执行】
    int expire() default 600;

    // 已经在执行时的提示信息
    String processingMsg() default "request already being processed";

    // 执行时返回的信息
    String successMsg() default "request has been processed successfully";
}
