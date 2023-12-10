package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataSource {
    QUESTION("question"),
    USER("user"),
    ARTICLE("article");

    private final String value;
}
