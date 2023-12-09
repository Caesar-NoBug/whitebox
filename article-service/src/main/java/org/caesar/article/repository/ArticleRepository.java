package org.caesar.article.repository;

import org.caesar.article.model.entity.Article;
import org.caesar.article.model.entity.ArticleOps;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository {

    // 添加文章
    boolean addArticle(Article article);

    // 获取文章详情(不包含用户对文章的操作)
    Article getArticle(long articleId);

    // 获取用户对文章的操作（点赞、点踩、收藏）
    ArticleOps getArticleOps(long userId, long articleId);

    // 判断文章是否存在
    boolean existArticle(long articleId);

    // 添加阅读记录
    boolean addViewHistory(long userId, long articleId, LocalDateTime viewAt);

    /**
     * 获取近期修改的文章
     * @param afterTime 开始统计变更的时间
     * @return 文章列表
     */
    List<Article> getUpdatedArticle(LocalDateTime afterTime);

    // 获取文章历史
    List<Article> getArticleHistory(long userId, Integer from, Integer size);

    // 文章是否属于用户
    boolean hasOwnership(long userId, long article);

    // 修改文章
    boolean updateArticle(long userId, Article updatedArticle);

    // 删除文章
    boolean deleteArticle(long userId, long articleId);

    /**
     * 评价文章
     * @param userId    用户id
     * @param articleId 文章id
     * @param mark      评价(-1:踩，0:无，1:赞)
     * @return          文章评价是否有更新
     */
    boolean markArticle(long userId, long articleId, int mark);

    // 文章收藏
    boolean favorArticle(long userId, long articleId, boolean isFavor);
}
