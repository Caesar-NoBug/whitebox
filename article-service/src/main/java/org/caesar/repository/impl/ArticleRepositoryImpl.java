package org.caesar.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.repository.ArticleRepository;
import org.caesar.repository.CommentRepository;
import org.caesar.task.HotArticleTask;
import org.caesar.util.RedisKey;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.repository.CacheRepository;
import org.caesar.domain.article.enums.ElementType;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.mapper.ArticleMapper;
import org.caesar.model.MsArticleStruct;
import org.caesar.model.entity.Article;
import org.caesar.model.entity.ArticleOps;
import org.caesar.model.po.ArticlePO;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
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
        long articleId = article.getId();
        cacheRepo.setLongValue(RedisKey.articleLikeCount(articleId), 0);
        cacheRepo.setLongValue(RedisKey.articleFavorCount(articleId), 0);

        return true;
    }

    @Override
    public Article getArticle(long articleId) {

        Article article = articleStruct.POtoDO(getById(articleId));

        // 获取文章的浏览量、点赞量、收藏量
        article.setViewNum(cacheRepo.getLogLogCount(RedisKey.articleViewCount(articleId)));
        article.setFavorNum(cacheRepo.getLongValue(RedisKey.articleFavorCount(articleId)));
        article.setLikeNum(cacheRepo.getLongValue(RedisKey.articleLikeCount(articleId)));

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
        cacheRepo.addLogLogElement(RedisKey.articleViewCount(articleId), userId);

        String historyKey = RedisKey.articleHistorySet(articleId);

        // 若该文章还没有浏览历史，说明也不在近期文章中，则加入近期文章列表
        if(!cacheRepo.exist(historyKey)) {
            cacheRepo.getSortedSet(RedisKey.recentArticleSet()).add(articleId, 0);
        }

        // 在对应文章的浏览记录中添加一条记录
        BoundZSetOperations<Object, Object> historySet = cacheRepo.getSortedSet(historyKey);

        historySet.add(userId, viewAt.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000 - HotArticleTask.BEGIN_TIMESTAMP);

        return baseMapper.addViewHistory(userId, articleId, viewAt) > 0;
    }

    @Override
    public List<Article> getUpdatedArticle(LocalDateTime afterTime) {
        return baseMapper.getUpdatedArticle(afterTime)
                .stream()
                .map(this::loadArticleOps)
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
    public List<Article> getArticleMin(Set<Long> articleIds) {
        return baseMapper.getArticleMin(articleIds).stream()
                .map(this::loadArticleOps)
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

    //TODO: 加一个延迟双删操作
    @Override
    public boolean updateArticle(long userId, Article article) {
        // 删除文章缓存
        cacheRepo.deleteObject(RedisKey.cacheArticle(article.getId()));

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
        flag &= cacheRepo.deleteLogLog(RedisKey.articleViewCount(articleId));
        flag &= cacheRepo.deleteLong(RedisKey.articleFavorCount(articleId));
        flag &= cacheRepo.deleteLong(RedisKey.articleLikeCount(articleId));

        ThrowUtil.ifFalse(flag, ErrorCode.SYSTEM_ERROR, "删除文章操作记录失败");

        // 删除评论
        commentRepo.deleteComment(ElementType.ARTICLE.getValue(), articleId);

        return true;
    }

    @Override
    public boolean markArticle(long userId, long articleId, int mark) {

        // 如果没有不同的评价（即评价已经是mark了），无需修改
        if (!baseMapper.hasDiffArticleMark(userId, articleId, mark)) return false;

        if (mark == -1)
            cacheRepo.decrLong(RedisKey.articleLikeCount(articleId));
        else if (mark == 1)
            cacheRepo.incrLong(RedisKey.articleLikeCount(articleId));

        return baseMapper.markArticle(userId, articleId, mark) > 0;
    }

    @Override
    public boolean favorArticle(long userId, long articleId, boolean isFavor) {

        // 如果没有不同的收藏状态（即状态已经是isFavor了），无需修改
        if (!baseMapper.hasDiffArticleFavor(userId, articleId, isFavor)) return false;

        if (isFavor)
            cacheRepo.decrLong(RedisKey.articleFavorCount(articleId));
        else
            cacheRepo.incrLong(RedisKey.articleFavorCount(articleId));

        return baseMapper.favorArticle(userId, articleId, isFavor) > 0;
    }


    private Article loadArticleOps(ArticlePO articlePO) {
        Article article = articleStruct.POtoDO(articlePO);
        long id = article.getId();
        article.setViewNum(cacheRepo.getLogLogCount(RedisKey.articleViewCount(id)));
        article.setFavorNum(cacheRepo.getLongValue(RedisKey.articleFavorCount(id)));
        article.setLikeNum(cacheRepo.getLongValue(RedisKey.articleLikeCount(id)));
        return article;
    }

}
