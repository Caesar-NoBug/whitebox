package org.caesar.domain.executor.enums;

/**
 * JUDGING:             判题中
 * ACCEPTED:            代码运行无误且结果正确
 * WRONG_ANSWER:        代码输出与预期不符
 * TIME_LIMITED:        代码运行超时
 * MEMORY_LIMITED:      代码运行占用内存过多
 * COMPILE_ERROR:       代码编译错误
 * RUNTIME_ERROR:       代码运行时错误
 * SYSTEM_ERROR:        系统内部错误
 * TEMPORARY_ACCEPTED:  暂时接受代码（运行无误，但尚未校验结果是否正确的中间状态）
 */
public enum CodeResultType {
    JUDGING,
    ACCEPTED,
    WRONG_ANSWER,
    TIME_LIMITED,
    MEMORY_LIMITED,
    COMPILE_ERROR,
    RUNTIME_ERROR,
    SYSTEM_ERROR,
    TEMPORARY_ACCEPTED
}
