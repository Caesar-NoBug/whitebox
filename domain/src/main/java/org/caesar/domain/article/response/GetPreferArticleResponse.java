package org.caesar.domain.article.response;

import lombok.Data;
import org.caesar.domain.article.vo.ArticleMinVO;

import java.util.List;

@Data
public class GetPreferArticleResponse {

    // 近期浏览过的文章
    private List<ArticleMinVO> viewedArticles;

    // 近期点赞或收藏过的文章
    private List<ArticleMinVO> preferredArticles;

    // 随机获取点赞或收藏过的文章
    private List<ArticleMinVO> randPreferredArticles;

}
