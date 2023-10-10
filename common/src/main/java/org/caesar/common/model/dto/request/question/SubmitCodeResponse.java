package org.caesar.common.model.dto.request.question;

import lombok.Data;
import org.caesar.common.constant.enums.CodeResultType;

import java.util.List;

/**
 * id:          提交请求id
 * success:     代码是否正确
 * type:        代码运行结果类型
 * message:     第一个错误代码错误信息
 * answer:      第一个错误代码的标准答案
 * time:        执行时间（ms）
 */
@Data
public class SubmitCodeResponse {

    private long id;

    private boolean success;

    private List<CodeResultType> type;

    private String message;

    private List<String> result;

    private List<Long> time;
}
