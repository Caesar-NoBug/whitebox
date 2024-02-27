package org.caesar.domain.question.request;

import lombok.Data;
import org.caesar.domain.executor.enums.CodeLanguage;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SubmitCodeRequest implements Serializable {

    // 提交id
    @NotNull
    private Integer submitId;

    // 问题id
    @Min(0)
    private Long questionId;

    @Length(max = 2048)
    private String code;

    @NotNull
    private CodeLanguage language;
}
