package org.caesar.domain.article.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleType {
    BLOG(0), SOLUTION(1);

    private final int value;
}
