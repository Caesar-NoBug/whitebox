package org.caesar.common.check.checker;

import org.caesar.domain.common.enums.StrFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * name:        字段名称
 * length:      字段长度
 * maxLength:   字段允许的最大长度
 * type:        字段类型（用于匹配字段格式）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StringChecker {
    String name() default "";
    int length() default -1;
    int maxLength() default 4096;
    //TODO: 优化成Pattern的形式
    StrFormat format() default StrFormat.DEFAULT;
}
