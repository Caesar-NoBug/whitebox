package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuestionSortField implements SortField{

    LIKE_NUM("likeNum"),
    FAVOR_NUM("favorNum"),
    SUBMIT_NUM("submitNum");

    private final String value;
}
