package org.caesar.model;

import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.model.entity.Article;
import org.caesar.model.entity.ArticleHistory;
import org.caesar.model.po.ArticlePO;
import org.caesar.domain.search.vo.ArticleIndexVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsArticleStruct {

    Article VOtoDO(ArticleVO articleVO);
    Article POtoDO(ArticlePO articlePO);
    ArticlePO DOtoPO(Article article);
    ArticleVO DOtoVO(Article article);
    ArticleMinVO DOtoMinVO(Article article);
    ArticleIndexVO DOtoIndex(Article article);
    ArticleHistoryVO DOtoHistoryVO(ArticleHistory history);
}
