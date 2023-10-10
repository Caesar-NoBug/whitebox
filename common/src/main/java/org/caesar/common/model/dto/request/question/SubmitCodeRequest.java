package org.caesar.common.model.dto.request.question;

import lombok.Data;
import org.caesar.common.check.checker.NumberChecker;
import org.caesar.common.check.checker.ObjectChecker;
import org.caesar.common.check.checker.StringChecker;
import org.caesar.common.constant.enums.CodeLanguage;

@Data
public class SubmitCodeRequest {

    @NumberChecker(name = "提交id", min = 0)
    private long id;

    @NumberChecker(name = "问题id", min = 0)
    private long qId;

    @StringChecker(name = "用户代码")
    private String code;

    @ObjectChecker(name = "代码语言")
    private CodeLanguage language;
}
