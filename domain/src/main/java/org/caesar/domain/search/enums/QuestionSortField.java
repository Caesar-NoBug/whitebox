package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.caesar.domain.search.vo.QuestionIndex;

@Getter
@AllArgsConstructor
public enum QuestionSortField implements SortField{

    LIKE_NUM(QuestionIndex.Fields.likeNum),
    FAVOR_NUM(QuestionIndex.Fields.favorNum),
    SUBMIT_NUM(QuestionIndex.Fields.submitNum);

    private final String value;
}
