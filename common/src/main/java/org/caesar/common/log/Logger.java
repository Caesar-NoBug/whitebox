package org.caesar.common.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Logger {

    // 接口名称
    String value() default "";

    // 是否打印基本访问日志
    boolean visit() default true;

    // 是否打印参数
    boolean args() default false;

    // 是否打印返回值
    boolean result() default false;

    // 是否打印执行时间
    boolean time() default false;
}
