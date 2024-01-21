package org.caesar.domain.question.request;

import lombok.Data;
import org.caesar.domain.executor.enums.CodeLanguage;

import java.io.Serializable;

@Data
public class SubmitCodeRequest implements Serializable {

    private Long qId;

    //@StringChecker(name = "用户代码")
    private String code;

    //@ObjectChecker(name = "代码语言")
    private CodeLanguage language;
}
