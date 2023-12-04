package org.caesar.domain.article.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ElementType {

    ARTICLE(0), COMMENT(1), QUESTION(2);

    private final int value;
}
