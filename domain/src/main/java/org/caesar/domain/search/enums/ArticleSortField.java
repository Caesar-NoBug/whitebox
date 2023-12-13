package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.caesar.domain.search.vo.ArticleIndex;
import org.caesar.domain.search.vo.QuestionIndex;

@Getter
@AllArgsConstructor
public enum ArticleSortField implements SortField{

    LIKE_NUM(ArticleIndex.Fields.likeNum),
    FAVOR_NUM(ArticleIndex.Fields.favorNum),
    VIEW_NUM(ArticleIndex.Fields.viewNum),
    UPDATE_AT(ArticleIndex.Fields.updateAt);

    private final String value;
}
