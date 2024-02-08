package org.caesar.domain.aigc.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class QuestionHelperRequest {

    /**
     * 问题描述
     */
    private String description;

    /**
     * 用户代码
     */
    @Length(max = 2048)
    private String code;

    /**
     * 正确代码
     */
    @Length(max = 2048)
    private String correctCode;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 用户代码执行的结果
     */
    private String result;
}
