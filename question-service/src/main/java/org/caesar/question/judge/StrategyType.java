package org.caesar.question.judge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StrategyType {
    DEFAULT(0),
    RANGE(1),
    UNORDERED(2);

    private final int value;
}
