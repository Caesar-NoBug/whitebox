package org.caesar.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * codeResult: 代码运行结果
 * outputCase: 输出用例
 * type: 判题类型
 */
@Data
@AllArgsConstructor
public class JudgeParam {
    private List<String> codeResult;
    private List<String> outputCase;
    private int type;
}
