package org.caesar.common.context;

import org.caesar.common.exception.ThrowUtil;
import org.springframework.stereotype.Component;

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

    public static void setUserId(long userId) {
        set(USER_ID, userId);
    }

    public static long getUserId() {
        Long userId = get(USER_ID);
        ThrowUtil.ifNull(userId, "用户未登录");
        return userId;
    }

    public static void setTraceId(long traceId) {
        set(TRACE_ID, traceId);
    }

    public static String getTraceId() {
        return get(TRACE_ID);
    }

    public static void clear() {
        threadLocal.remove();
    }

}
