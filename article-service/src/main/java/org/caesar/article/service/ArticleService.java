package org.caesar.article.service;

import org.caesar.domain.article.request.AddArticleRequest;
import org.caesar.domain.article.request.UpdateArticleRequest;
import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.article.model.entity.Article;

import java.time.LocalDateTime;
import java.util.List;

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
}
