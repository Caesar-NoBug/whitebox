package org.caesar.common.context;

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

    public static void clear() {
        threadLocal.remove();
    }

}
