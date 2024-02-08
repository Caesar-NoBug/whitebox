package org.caesar.article.service;

import org.caesar.domain.article.request.AddArticleRequest;
import org.caesar.domain.article.request.UpdateArticleRequest;
import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.article.model.entity.Article;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
* @author caesar
* @description 针对表【article】的数据库操作Service
* @createDate 2023-11-29 20:02:28
*/
public interface ArticleService {

    // 添加文章
    void addArticle(long userId, AddArticleRequest request);

    // 查看文章
    ArticleVO viewArticle(long userId, long articleId);

    // 获取近期修改了的文章
    List<Article> getUpdatedArticle(LocalDateTime afterTime);

    // 获取近期喜欢的文章
    GetPreferArticleResponse getPreferArticle(long userId, int viewedSize, int preferredSize, int randPreferredSize);

    // 获取热门文章
    List<ArticleMinVO> getHotArticle();

    // 获取文章简略信息
    List<ArticleMinVO> getArticleMin(Set<Long> articleIds);

    // 查看文章历史
    List<ArticleHistoryVO> getArticleHistory(long userId, Integer from, Integer size);

    // 修改文章
    void updateArticle(long userId, UpdateArticleRequest request);

    // 删除文章
    void deleteArticle(long userId, long articleId);

    // 评价文章(-1:踩，0:无，1:赞)
    void markArticle(long userId, long articleId, int mark);

    // 文章收藏
    void favorArticle(long userId, long articleId, boolean isFavor);

    // 文章去重（出除用户已经看过的文章）
    List<Long> getUniqueArticle(long userId, List<Long> articleIds);
}
