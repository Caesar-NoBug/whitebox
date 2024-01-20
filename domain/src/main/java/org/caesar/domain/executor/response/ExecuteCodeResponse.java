package org.caesar.domain.executor.response;

import lombok.Data;
import org.caesar.domain.executor.enums.CodeResultType;

import java.util.List;

/**
 * success:     代码运行无异常发生
 * type:        代码运行结果类型
 * result:      执行结果
 * message:     第一个错误代码错误信息
 * time:        执行时间（ms）
 * memory:      运行内存（MB）
 */
@Data
public class ExecuteCodeResponse {
    private String id;
    private boolean success;
    private List<CodeResultType> type;
    private String message;
    private List<String> result;
    private List<Long> time;
    private List<Long> memory;
}

