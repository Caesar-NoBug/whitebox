package org.caesar.common.check;

import lombok.Data;
import org.caesar.common.check.checker.NumberChecker;
import org.caesar.common.check.checker.ObjectChecker;
import org.caesar.common.check.checker.StringChecker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;


/**
 * @ClassName ObjectInfo
 * @Description 用于储存构造对象所需信息
 */
@Data
public class ObjectInfo {

    private Class<?>[] filedTypes;
    private Method[] getters;
    private Annotation[] checkers;

    public ObjectInfo(Class<?> clazz) throws NoSuchMethodException {

        Field[] fields = clazz.getDeclaredFields();
        int fieldCount = fields.length;

        filedTypes = new Class[fieldCount];
        getters = new Method[fieldCount];
        checkers = new Annotation[fieldCount];

        for (int i = 0; i < fieldCount; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            checkers[i] = getChecker(field);
            filedTypes[i] = field.getType();
            String name = field.getName();
            getters[i] = clazz.getMethod("get" +
                    Character.toUpperCase(name.charAt(0)) + name.substring(1));
        }

    }

    private Annotation getChecker(Field field) {

        Annotation annotation;
        annotation = field.getAnnotation(ObjectChecker.class);
        if(!Objects.isNull(annotation))
            return annotation;

        annotation = field.getAnnotation(StringChecker.class);
        if(!Objects.isNull(annotation))
            return annotation;

        annotation = field.getAnnotation(NumberChecker.class);
        if(!Objects.isNull(annotation))
            return annotation;

        return null;
    }
}
