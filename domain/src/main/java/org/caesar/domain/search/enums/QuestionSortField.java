package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.caesar.domain.search.vo.QuestionIndexVO;

@Getter
@AllArgsConstructor
public enum QuestionSortField implements SortField{

    DIFFICULTY("DIFFICULTY"),
    LIKE_NUM("LIKE_NUM"),
    FAVOR_NUM("FAVOR_NUM"),
    SUBMIT_NUM("SUBMIT_NUM");

    private final String value;

    public static QuestionSortField of(String value) {
        for (QuestionSortField sortField : QuestionSortField.values()) {
            if (sortField.getValue().equals(value)) {
                return sortField;
            }
        }
        return null;
    }
}
