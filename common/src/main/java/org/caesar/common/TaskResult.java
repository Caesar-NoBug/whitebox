package org.caesar.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @param <T>   任务结果的类型
 * isSuccess:   任务是否执行成功
 * message:     任务执行失败的错误信息
 * data:        任务执行结果
 */
@Data
@AllArgsConstructor
public class TaskResult<T> {
    private boolean isSuccess;
    private String message;
    private T data;

    public static <T> TaskResult <T> ok(T data) {
        return new TaskResult<>(true, null, data);
    }

    public static <T> TaskResult <T> ok(T data, String message) {
        return new TaskResult<>(true, message, data);
    }

    public static <T> TaskResult <T> err(String message) {
        return new TaskResult<>(false, message, null);
    }
}
