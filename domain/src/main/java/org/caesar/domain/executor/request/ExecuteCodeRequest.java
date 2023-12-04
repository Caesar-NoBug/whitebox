package org.caesar.domain.executor.request;

import lombok.*;
import org.caesar.domain.executor.enums.CodeLanguage;


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

    //@StringChecker(name = "code", maxLength = 8192)
    private String code;

    //@ObjectChecker(name = "代码语言")
    private CodeLanguage language;

    private List<String> inputCase;

    private Long timeLimit;
    private Long memoryLimit;

}
