package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.caesar.domain.search.vo.ArticleIndexVO;

@Getter
@AllArgsConstructor
public enum ArticleSortField implements SortField{

    LIKE_NUM("LIKE_NUM"),
    FAVOR_NUM("FAVOR_NUM"),
    VIEW_NUM("VIEW_NUM"),
    UPDATE_AT("UPDATE_AT");

    private final String value;

    public static ArticleSortField of(String value) {
        for (ArticleSortField sortField : ArticleSortField.values()) {
            if (sortField.getValue().equals(value)) {
                return sortField;
            }
        }
        return null;
    }

}
