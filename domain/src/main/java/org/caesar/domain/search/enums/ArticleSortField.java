package org.caesar.domain.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.caesar.domain.search.vo.ArticleIndexVO;

@Getter
@AllArgsConstructor
public enum ArticleSortField implements SortField{

    LIKE_NUM(ArticleIndexVO.Fields.likeNum),
    FAVOR_NUM(ArticleIndexVO.Fields.favorNum),
    VIEW_NUM(ArticleIndexVO.Fields.viewNum),
    UPDATE_AT(ArticleIndexVO.Fields.updateAt);

    private final String value;
}
