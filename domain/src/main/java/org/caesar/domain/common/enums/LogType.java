package org.caesar.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LogType {

    /**
     * 业务逻辑
     */
    BUSINESS_LOG("Business Log"),

    /**
     * 基础访问日志
     */
    VISIT_LOG("Visit Log"),

    /**
     * 业务摘要
     */
    DIGEST_LOG("Digest Log"),

    /**
     * 方法入参
     */
    METHOD_ARGS("Method args"),

    /**
     * 方法返回值
     */
    METHOD_RESULT("Method Result"),

    /**
     *  方法执行时间
     */
    METHOD_EXECUTE_TIME("Method Execute Time"),

    /**
     * 系统启动
     */
    SYSTEM_INIT("System Init"),

    /**
     * 远程服务调用
     */
    RPC("RPC"),

    /**
     * 消费统计
     */
    COST_LOG("Cost Log"),

    /**
     * 登录日志
     */
    LOGIN("Login"),

    /**
     * 登出日志
     */
    LOGOUT("Logout");

    private final String value;
}
