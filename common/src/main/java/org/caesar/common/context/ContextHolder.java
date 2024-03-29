package org.caesar.common.context;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.str.StrUtil;
import org.caesar.domain.common.enums.ErrorCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存当前线程的相关信息
 */
public class ContextHolder {

    public static final String USER_ID = "userId";
    public static final String TRACE_ID = "traceId";
    public static final String BUSINESS_NAME = "business";

    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static <T> void set(String key, T value) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static <T> T get(String key) {
        Map<String, Object> map = threadLocal.get();
        return map == null ? null : (T) map.get(key);
    }

    public static void setUserId(Long userId) {
        set(USER_ID, userId);
    }

    // 获取用户ID, 如果为空抛出异常
    public static Long getUserIdNecessarily() {
        Long userId = get(USER_ID);
        ThrowUtil.ifNull(userId, ErrorCode.NOT_AUTHENTICATED_ERROR, "Please login.");
        return userId;
    }

    // 获取用户ID, 如果为空返回null(不抛出异常)
    public static Long getUserId() {
        return get(USER_ID);
    }

    public static void setTraceId(String traceId) {
        set(TRACE_ID, traceId);
    }

    public static String getTraceId() {
        return get(TRACE_ID);
    }

    public static void setBusinessName(String businessName) {
        set(BUSINESS_NAME, businessName);
    }

    public static String getBusinessName() {
        String businessName = get(BUSINESS_NAME);
        return StrUtil.isBlank(businessName) ? "System" : businessName;
    }

    public static void clear() {
        threadLocal.remove();
    }

}
