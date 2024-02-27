package org.caesar.domain.question.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.domain.executor.enums.CodeLanguage;
import org.caesar.domain.executor.enums.SubmitCodeResultType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * code:        用户代码
 * language:    代码语言
 * result:      用户代码运行结果(仅保留出错的那一个用例对应的运行结果)
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
public class SubmitCodeResultVO {

    private Long userId;
    private Long questionId;
    private Integer submitId;
    private String code;
    private CodeLanguage language;
    private String result;
    private int status;
    private List<SubmitCodeResultType> type;
    private String message;
    private List<Long> time;
    private List<Long> memory;
    private LocalDateTime createAt;
}
