package org.caesar.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.repository.CacheRepository;
import org.caesar.constant.RedisPrefix;
import org.caesar.domain.article.enums.ElementType;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.mapper.ArticleMapper;
import org.caesar.model.MsArticleStruct;
import org.caesar.model.entity.Article;
import org.caesar.model.entity.ArticleOps;
import org.caesar.model.po.ArticlePO;
import org.caesar.repository.ArticleRepository;
import org.caesar.repository.CommentRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ArticleRepositoryImpl extends ServiceImpl<ArticleMapper, ArticlePO>
        implements ArticleRepository {

    @Resource
    private MsArticleStruct articleStruct;

    @Resource
    private CommentRepository commentRepo;

    @Resource
    private CacheRepository cacheRepo;

    @Override
    public boolean addArticle(Article article) {

        ThrowUtil.ifFalse(save(articleStruct.DOtoPO(article)), ErrorCode.SYSTEM_ERROR, "添加文章失败");

        cacheRepo.setLongValue(RedisPrefix.ARTICLE_FAVOR_COUNT, 0);
        cacheRepo.setLongValue(RedisPrefix.ARTICLE_LIKE_COUNT, 0);

        return true;
    }

    @Override
    public Article getArticle(long articleId) {

        Article article = articleStruct.POtoDO(getById(articleId));

        // 获取文章的浏览量、点赞量、收藏量
        article.setViewNum(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + articleId));
        article.setFavorNum(cacheRepo.getLongValue(RedisPrefix.ARTICLE_FAVOR_COUNT + articleId));
        article.setLikeNum(cacheRepo.getLongValue(RedisPrefix.ARTICLE_LIKE_COUNT + articleId));

        return article;
    }

    @Override
    public ArticleOps getArticleOps(long userId, long articleId) {
        return baseMapper.getArticleOps(userId, articleId);
    }

    @Override
    public boolean existArticle(long articleId) {
        return baseMapper.selectCount(
                new QueryWrapper<ArticlePO>()
                        .eq("id", articleId)
        ) == 1;
    }

    public boolean addViewHistory(long userId, long articleId, LocalDateTime viewAt) {
        cacheRepo.addLogLogElement(RedisPrefix.ARTICLE_VIEW_COUNT + articleId, userId);
        return baseMapper.addViewHistory(userId, articleId, viewAt) > 0;
    }

    @Override
    public List<Article> getArticleList(int from, int size) {
        return page(new Page<>(from, size)).getRecords()
                .stream()
                .map(articleStruct::POtoDO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getArticleHistory(long userId, Integer from, Integer size) {
        int offset = from * size;
        return baseMapper.getArticleHistory(userId, offset, size).stream()
                .map(articleStruct::POtoDO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasOwnership(long userId, long articleId) {
        return baseMapper.selectCount(
                new QueryWrapper<ArticlePO>()
                        .eq("id", articleId)
                        .eq("create_by", userId)
        ) > 0;
    }

    @Override
    public boolean updateArticle(long userId, Article article) {
        return update(articleStruct.DOtoPO(article),
                new UpdateWrapper<ArticlePO>().eq("id", article.getId()).eq("create_by", userId));
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(value = 500, maxDelay = 2000, multiplier = 2))
    @Transactional
    @Override
    public boolean deleteArticle(long userId, long articleId) {
        ThrowUtil.ifFalse(
                remove(
                        new QueryWrapper<ArticlePO>()
                                .eq("id", articleId)
                                .eq("create_by", userId))
                , "文章不存在或无权限删除");

        // 删除文章操作记录
        baseMapper.deleteArticleOps(articleId);

        // 删除浏览、点赞、收藏记录
        boolean flag = true;
        flag &= cacheRepo.deleteLogLog(RedisPrefix.ARTICLE_VIEW_COUNT + articleId);
        flag &= cacheRepo.deleteLong(RedisPrefix.ARTICLE_FAVOR_COUNT + articleId);
        flag &= cacheRepo.deleteLong(RedisPrefix.ARTICLE_LIKE_COUNT + articleId);

        // 删除评论
        commentRepo.deleteComment(ElementType.ARTICLE.getValue(), articleId);

        return true;
    }

    @Override
    public boolean markArticle(long userId, long articleId, int mark) {

        // 如果没有不同的评价（即评价已经是mark了），无需修改
        if (!baseMapper.hasDiffArticleMark(userId, articleId, mark)) return false;

        if (mark == -1)
            cacheRepo.decrLong(RedisPrefix.ARTICLE_LIKE_COUNT + articleId);
        else if (mark == 1)
            cacheRepo.incrLong(RedisPrefix.ARTICLE_LIKE_COUNT + articleId);

        return baseMapper.markArticle(userId, articleId, mark) > 0;
    }

    @Override
    public boolean favorArticle(long userId, long articleId, boolean isFavor) {

        // 如果没有不同的收藏状态（即状态已经是isFavor了），无需修改
        if (!baseMapper.hasDiffArticleFavor(userId, articleId, isFavor)) return false;

        if (isFavor)
            cacheRepo.decrLong(RedisPrefix.ARTICLE_FAVOR_COUNT + articleId);
        else
            cacheRepo.incrLong(RedisPrefix.ARTICLE_FAVOR_COUNT + articleId);

        return baseMapper.favorArticle(userId, articleId, isFavor) > 0;
    }

}
