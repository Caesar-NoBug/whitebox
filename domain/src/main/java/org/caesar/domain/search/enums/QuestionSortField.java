package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.caesar.domain.search.vo.QuestionIndexVO;

@Getter
@AllArgsConstructor
public enum QuestionSortField implements SortField{

    LIKE_NUM(QuestionIndexVO.Fields.likeNum),
    FAVOR_NUM(QuestionIndexVO.Fields.favorNum),
    SUBMIT_NUM(QuestionIndexVO.Fields.submitNum);

    private final String value;
}
