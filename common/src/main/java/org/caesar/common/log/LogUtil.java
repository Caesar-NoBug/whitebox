package org.caesar.common.log;

import lombok.extern.slf4j.Slf4j;
import org.caesar.common.context.ContextHolder;
import org.caesar.domain.common.enums.LogType;

import java.util.Date;

@Slf4j
public class LogUtil {

    /**
     * 错误信息，系统内部错误，但是不影响系统其他功能
     */
    public static void error(String message, Throwable e) {
        log.error(message, e);
    }

    public static void error(boolean isLog, String message, Throwable e) {
        if(isLog) error(message, e);
    }

    /**
     * 警告信息，如参数不合法，不符合预期等
     */
    public static void warn(String message) {
        log.warn(message);
    }

    public static void warn(String message, Throwable e) {
        log.warn(message, e);
    }

    public static void warn(boolean isLog, String message) {
        if(isLog) warn(message);
    }

    /**
     * 正常执行流程记录相关信息
     */
    public static void info(LogType type, String message) {
        log.info("[{}] '{}': {}", type.getValue(), ContextHolder.getBusinessName(), message);
    }

    public static void info(boolean isLog, LogType type, String message) {
        if(isLog) info(type, message);
    }

    /**
     * 打印业务日志【INFO级别】
     * @param message 日志信息
     */
    public static void bizLog(String message) {
        info(LogType.BUSINESS_LOG, message);
    }

    public static void bizLog(boolean isLog, String message) {
        if(isLog) bizLog(message);
    }

    /**
     * 系统严重错误，无法继续运行，如内存溢出，磁盘满，磁盘IO异常等
     */
    public static void fatal(String message) {

    }

    public static void fatal(boolean isLog, String message) {
        if(isLog) fatal(message);
    }
}
