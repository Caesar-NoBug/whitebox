package org.caesar.domain.executor.response;

import lombok.Data;
import org.caesar.domain.executor.enums.SubmitCodeResultType;

import java.util.List;

/**
 * questionId:  题目id
 * submitId:    提交id
 * success:     代码运行无异常发生
 * type:        代码运行结果类型
 * result:      执行结果
 * message:     第一个错误代码错误信息
 * time:        执行时间（ms）
 * memory:      运行内存（MB）
 */
@Data
public class ExecuteCodeResponse {
    private long questionId;
    private int submitId;
    private boolean success;
    private List<SubmitCodeResultType> type;
    private String message;
    private List<String> result;
    private List<Long> time;
    private List<Long> memory;
}

