package org.caesar.common.log;

import lombok.extern.slf4j.Slf4j;
import org.caesar.common.context.ContextHolder;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.enums.LogType;

import java.util.Date;

@Slf4j
public class LogUtil {

    public static final String LOG_FORMAT = "[%s] '%s': {%s}";

    /**
     * 错误信息，系统内部错误，但是不影响系统其他功能
     */
    public static void error(ErrorCode code, String message, Throwable e) {
        log.error(toLogString(code, message), e);
    }

    public static void error(boolean isLog, ErrorCode code,String message, Throwable e) {
        if(isLog) error(code, message, e);
    }

    /**
     * 警告信息，如参数不合法，不符合预期等
     */
    public static void warn(ErrorCode code, String message) {
        log.warn(toLogString(code, message));
    }

    public static void warn(ErrorCode code, String message, Throwable e) {
        log.warn(toLogString(code, message), e);
    }

    public static void warn(boolean isLog, ErrorCode code, String message) {
        if(isLog) warn(code, message);
    }

    /**
     * 正常执行流程记录相关信息
     */
    public static void info(LogType type, String message, Object... params) {
        log.info(toLogString(type, message), params);
    }

    public static void info(boolean isLog, LogType type, String message) {
        if(isLog) info(type, message);
    }

    /**
     * 打印业务日志【INFO级别】
     * @param message 日志信息
     */
    public static void bizLog(String message, Object... params) {
        info(LogType.BUSINESS_LOG, message, params);
    }

    public static void bizLog(boolean isLog, String message) {
        if(isLog) bizLog(message);
    }

    private static String toLogString(LogType type, String message) {
        return String.format(LOG_FORMAT, type.getValue(), ContextHolder.getBusinessName(), message);
    }

    private static String toLogString(ErrorCode code, String message) {
        return String.format(LOG_FORMAT, code.getMessage(), ContextHolder.getBusinessName(), message);
    }
}
