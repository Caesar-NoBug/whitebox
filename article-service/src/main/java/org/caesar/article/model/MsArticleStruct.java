package org.caesar.article.model;

import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.article.model.entity.Article;
import org.caesar.article.model.po.ArticlePO;
import org.caesar.domain.search.vo.ArticleIndex;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsArticleStruct {

    Article VOtoDO(ArticleVO articleVO);
    Article POtoDO(ArticlePO articlePO);
    ArticlePO DOtoPO(Article article);
    ArticleVO DOtoVO(Article article);
    ArticleMinVO DOtoMinVO(Article article);
    ArticleIndex DOtoIndex(Article article);
    ArticleHistoryVO DOtoHistoryVO(Article article);
}
