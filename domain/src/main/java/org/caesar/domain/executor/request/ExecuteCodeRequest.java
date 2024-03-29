package org.caesar.domain.executor.request;

import lombok.*;
import org.caesar.domain.executor.enums.CodeLanguage;


import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * code: 待运行代码
 * inputCase: 输入用例
 * timeLimit: 运行时间限制
 * memoryLimit: 运行内存限制
 * ( 请求中本不应添加判题信息（运行时间、空间），但考虑到系统安全性和运行效率，附加这些信息可避免用户占用过多资源 )
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {

    //允许的最大运行时间为5s
    public static final long MAX_TIME_LIMIT = 5000;

    //允许的最大运行内存为256MB
    public static final long MAX_MEMORY_LIMIT = 1 << 8;

    // 问题id
    private long questionId;

    // 提交ID
    private int submitId;

    // 用户代码
    private String code;

    // 用户代码语言
    @NotNull
    private CodeLanguage language;

    // 输入用例
    private List<String> inputCase;

    // 执行时间限制
    @Max(MAX_TIME_LIMIT)
    private Long timeLimit;

    // 运行内存限制
    @Max(MAX_MEMORY_LIMIT)
    private Long memoryLimit;
}
