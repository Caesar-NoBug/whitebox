package org.caesar.domain.request.question;

import lombok.Data;
import org.caesar.domain.constant.enums.CodeLanguage;

import java.io.Serializable;

@Data
public class JudgeCodeRequest implements Serializable {

    //@NumberChecker(name = "用户id", min = 0)
    private long userId;

    //@NumberChecker(name = "问题id", min = 0)
    private long qId;

    //@StringChecker(name = "用户代码")
    private String code;

    //@ObjectChecker(name = "代码语言")
    private CodeLanguage language;
}
