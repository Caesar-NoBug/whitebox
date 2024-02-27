package org.caesar.question.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JudgeStatus {
    ACCEPTED(1),
    FAILED(0),
    JUDGING(-1);

    private final int code;
}
