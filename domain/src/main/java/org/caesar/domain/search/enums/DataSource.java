package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataSource {
    QUESTION(0),
    ARTICLE(1),
    USER(2);

    private final int value;
}
