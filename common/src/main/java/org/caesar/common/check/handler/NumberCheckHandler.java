package org.caesar.common.check.handler;

import org.caesar.common.check.checker.NumberChecker;
import org.caesar.common.constant.enums.ErrorCode;
import org.caesar.common.exception.BusinessException;

import java.lang.annotation.Annotation;

public class NumberCheckHandler extends CheckHandler {

    public static String OUT_OF_RANGE_MESSAGE = "参数数值超出允许范围";

    @Override
    public boolean match(Annotation checker) {
        return checker instanceof NumberChecker;
    }

    @Override
    public void doCheck(Object attribute, Annotation checker) {
        boolean accept = true;
        NumberChecker numberChecker = (NumberChecker) checker;

        if (attribute instanceof Integer) {

            int number = (int) attribute;
            accept = number >= numberChecker.min() && number <= numberChecker.max();

        } else if (attribute instanceof Long) {

            long number = (long) attribute;
            accept = number >= numberChecker.min() && number <= numberChecker.max();

        } else if (attribute instanceof Double) {

            double number = (double) attribute;
            accept = number >= numberChecker.min() && number <= numberChecker.max();

        } else if (attribute instanceof Float) {

            float number = (float) attribute;
            accept = number >= numberChecker.min() && number <= numberChecker.max();

        } else if (attribute instanceof Byte) {

            byte number = (byte) attribute;
            accept = number >= numberChecker.min() && number <= numberChecker.max();

        }

        if (!accept)
            throw new BusinessException(ErrorCode.ILLEGAL_PARAM_ERROR, numberChecker.name() + OUT_OF_RANGE_MESSAGE);
    }

}
