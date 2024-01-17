package org.caesar.common.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodUtil {

    // 缓存方法参数名称
    private static final Map<Method, List<String>> CACHE_PARAM_NAMES = new ConcurrentHashMap<>();

    public static List<String> getParamNames(Method method) {

        return CACHE_PARAM_NAMES.computeIfAbsent(method, m -> {
            List<String> paramNames = new ArrayList<>();

            for (Parameter parameter : m.getParameters()) {
                paramNames.add(parameter.getName());
            }

            return paramNames;
        });
    }
}
