package org.caesar.common.check;

import org.caesar.common.check.handler.CheckHandler;
import org.caesar.common.check.handler.NumberCheckHandler;
import org.caesar.common.check.handler.ObjectCheckHandler;
import org.caesar.common.check.handler.StringCheckHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CheckManager {

    //TODO: 使用AOP实现
    //TODO: 改成代码生成器的方案，以提高性能，另外，将校验逻辑移动到类的setter和constructor中
    public static final Map<Class<?>, ObjectInfo> OBJECT_INFO_MAP = new ConcurrentHashMap<>();
    public static CheckHandler checkHandlerChain;

    static {
        checkHandlerChain = new StringCheckHandler();
        checkHandlerChain.setNextHandler(new ObjectCheckHandler())
                .setNextHandler(new NumberCheckHandler());
    }

    //TODO: 自定义校验失败时返回的结果或异常
    public static void checkThrowException(Object object, Class<?> clazz) {

        ObjectInfo info = getObjectInfo(clazz);
        Method[] getters = info.getGetters();
        Annotation[] checkers = info.getCheckers();

        for (int i = 0; i < getters.length; i++) {
            try {
                Object attribute = getters[i].invoke(object);
                Annotation checker = checkers[i];

                CheckHandler checkHandler = checkHandlerChain;

                while (checkHandler != null) {

                    if(checkHandler.match(checker))
                        checkHandler.doCheck(attribute, checker);

                    checkHandler = checkHandler.getNext();
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private static ObjectInfo getObjectInfo(Class<?> clazz) {
        if (OBJECT_INFO_MAP.get(clazz) == null) {
            try {
                OBJECT_INFO_MAP.put(clazz, new ObjectInfo(clazz));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return OBJECT_INFO_MAP.get(clazz);
    }
}
