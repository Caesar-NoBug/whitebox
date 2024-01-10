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

    /**
     * 警告信息，如参数不合法，不符合预期等
     */
    public static void warn(String message, Throwable e) {
        log.warn(message, e);
    }

    /**
     * 正常执行流程记录相关信息
     */
    public static void info(LogType type, String message) {
        log.info("[{}] in '{}' {}", type.getValue(), ContextHolder.getBusinessName(), message);
    }

    public static void bizLog(String message) {
        info(LogType.BUSINESS_LOG, message);
    }

    /**
     * 系统严重错误，无法继续运行，如内存溢出，磁盘满，磁盘IO异常等
     */
    public static void fatal(String message) {

    }

}
