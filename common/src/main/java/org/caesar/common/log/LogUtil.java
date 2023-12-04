package org.caesar.common.log;

import lombok.extern.slf4j.Slf4j;
import org.caesar.common.context.ContextHolder;

import java.util.Date;

@Slf4j
public class LogUtil {

    /**
     * 错误信息，系统内部错误，但是不影响系统其他功能
     */
    public static void error(String message) {

    }

    /**
     * 警告信息，如参数不合法，不符合预期等
     */
    public static void warn(String message) {

    }

    /**
     * 正常执行流程记录相关信息
     */
    public static void info(String message) {

    }

    /**
     * 系统严重错误，无法继续运行，如内存溢出，磁盘满，磁盘IO异常等
     */
    public static void fatal(String message) {

    }

    private static String createLogMessage(String message) {
        String businessName = ContextHolder.get(ContextHolder.BUSINESS_NAME);
        Date time = new Date();
        //TODO: 日志格式配置到配置文件中
        return null;
    }
}
