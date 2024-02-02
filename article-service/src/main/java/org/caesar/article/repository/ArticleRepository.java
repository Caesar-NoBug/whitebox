package org.caesar.article.repository;

import org.caesar.article.model.entity.Article;
import org.caesar.article.model.entity.ArticleHistory;
import org.caesar.article.model.entity.ArticleOps;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ArticleRepository {

    // 添加文章
    boolean addArticle(Article article);

    // 获取文章详情(不包含用户对文章的操作)
    Article getArticle(long articleId);

    // 获取用户对文章的操作（点赞、点踩、收藏）
    ArticleOps getArticleOps(long userId, long articleId);

    List<Article> getRandPreferArticle(long userId, int size);

    // 文章去重（出除用户已经看过的文章）
    List<Long> getUniqueArticle(long userId, List<Long> articleIds);

    // 判断文章是否存在
    boolean existArticle(long articleId);

    // 添加阅读记录
    boolean addViewHistory(long userId, long articleId, LocalDateTime viewAt);

    // 获取近期点赞或收藏的文章
    List<Article> getRecentPreferredArticle(long userId, int size);

    // 获取近期阅读的文章
    List<Article> getRecentViewedArticle(long userId, int size);

    /**
     * 获取近期修改的文章
     * @param afterTime 开始统计变更的时间
     * @return 文章列表
     */
    List<Article> getUpdatedArticle(LocalDateTime afterTime);

    // TODO: 正确返回浏览时间记录
    // 获取文章历史
    List<ArticleHistory> getArticleHistory(long userId, Integer from, Integer size);

    // 获取文章列表(只获取少量信息)
    List<Article> getArticleMin(Set<Long> articleIds);

    // 文章是否属于用户
    boolean hasOwnership(long userId, long article);

    // 修改文章
    boolean updateArticle(long userId, Article updatedArticle);

    // 强制修改文章（系统级别）
    boolean updateArticle(Article updatedArticle);

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
