package org.caesar.common.check.checker;

import org.caesar.common.check.method.Checkable;
import org.caesar.common.check.method.NotNullCheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//用于自定义校验方法
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObjectChecker {
    String name() default "";
    Class<? extends Checkable> method() default NotNullCheck.class;
}
