package org.caesar.domain.question.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.domain.executor.enums.CodeResultType;

import java.util.List;

/**
 * complete:    是否判题结束完成
 * success:     代码是否正确
 * type:        代码运行结果类型
 * message:     第一个错误代码错误信息
 * time:        执行时间（ms）
 * memory:      运行内存（byte）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeCodeResponse {

    public static final JudgeCodeResponse JUDGING = new JudgeCodeResponse(false, false, null, null, null, null);

    private boolean complete;
    private boolean success;
    private List<CodeResultType> type;
    private String message;
    private List<Long> time;
    private List<Long> memory;

    public static JudgeCodeResponse judging() {
        return JUDGING;
    }
}
