package org.caesar.service;

import org.caesar.domain.aigc.request.RecommendArticleRequest;
import org.caesar.domain.article.vo.ArticleMinVO;

import java.util.List;

public interface RecommendService {
    List<ArticleMinVO> recommendArticle(long userId, RecommendArticleRequest request);
}
