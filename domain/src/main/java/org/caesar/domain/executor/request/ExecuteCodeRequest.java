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

    private String code;

    @NotNull
    private CodeLanguage language;

    private List<String> inputCase;

    @Max(MAX_TIME_LIMIT)
    private Long timeLimit;

    @Max(MAX_MEMORY_LIMIT)
    private Long memoryLimit;

}
