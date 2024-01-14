package org.caesar.common.exception;

import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.common.str.StrUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class ThrowUtil {

    //如果条件满足则抛出异常
    public static void ifTrue(boolean isThrow, RuntimeException e) {
        if (isThrow) throw e;
    }

    public static void ifFalse(boolean test, String message) {
        if (!test) throw new BusinessException(ErrorCode.ILLEGAL_PARAM_ERROR, message);
    }

    public static void ifFalse(boolean test, ErrorCode errorCode, String message) {
        if (!test) throw new BusinessException(errorCode, message);
    }

    public static void ifTrue(boolean isThrow, ErrorCode errorCode, String message) {
        if (isThrow) throw new BusinessException(errorCode, message);
    }

    public static void ifTrue(boolean isThrow, String message) {
        if (isThrow) throw new BusinessException(ErrorCode.ILLEGAL_PARAM_ERROR, message);
    }

    /**
     * @param validation 校验结果是否合法
     * @param message    错误信息
     *                   throw 校验异常
     */
    public static void validate(boolean validation, String message) {
        if (!validation) throw new ValidationException(message);
    }

    /**
     * 若对象为空则抛出异常
     *
     * @param object  待校验对象
     * @param message 抛出的异常信息
     */
    public static void ifNull(Object object, String message) {
        ifNull(object, ErrorCode.ILLEGAL_PARAM_ERROR, message);
    }

    /**
     * 若对象为空则抛出异常
     *
     * @param object  待校验对象
     * @param code    错误码
     * @param message 抛出的异常信息
     */
    public static void ifNull(Object object, ErrorCode code, String message) {
        if (Objects.isNull(object))
            throw new BusinessException(code, message);
    }

    public static <T, V> void ifEmpty(Map<T, V> map, ErrorCode code, String message) {
        if (Objects.isNull(map) || map.isEmpty())
            throw new BusinessException(code, message);
    }

    public static <T> void ifEmpty(Collection<T> collection, ErrorCode code, String message) {
        if (Objects.isNull(collection) || collection.isEmpty())
            throw new BusinessException(code, message);
    }

    /**
     * 若字符串不符合要求则抛出异常
     *
     * @param string  待校验字符串
     * @param message 错误信息
     */
    public static void testStr(String string, String message) {
        if (!StrUtil.checkString(string))
            throw new BusinessException(ErrorCode.ILLEGAL_PARAM_ERROR, message);
    }

    /**
     * 若字符串不符合要求则抛出异常
     *
     * @param string    待校验字符串
     * @param maxLength 待校验字符串允许的最大长度
     * @param message   错误信息
     */
    public static void testStr(String string, int maxLength, String message) {
        if (!StrUtil.checkString(string, maxLength))
            throw new BusinessException(ErrorCode.ILLEGAL_PARAM_ERROR, message);
    }

}
