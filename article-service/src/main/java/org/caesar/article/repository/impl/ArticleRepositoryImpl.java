package org.caesar.article.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.article.model.entity.ArticleHistory;
import org.caesar.article.repository.ArticleRepository;
import org.caesar.article.repository.CommentRepository;
import org.caesar.article.task.HotArticleTask;
import org.caesar.article.constant.CacheKey;
import org.caesar.common.batch.CacheIncBatchTaskHandler;
import org.caesar.common.batch.CacheIncTask;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.util.DataFilter;
import org.caesar.domain.article.enums.ElementType;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.article.mapper.ArticleMapper;
import org.caesar.article.model.MsArticleStruct;
import org.caesar.article.model.entity.Article;
import org.caesar.article.model.entity.ArticleOps;
import org.caesar.article.model.po.ArticlePO;
import org.springframework.data.redis.core.BoundZSetOperations;
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
    private DataFilter articleFilter;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private CacheIncBatchTaskHandler taskHandler;

    @Override
    public void addArticle(Article article) {
        ThrowUtil.ifFalse(save(articleStruct.DOtoPO(article)), ErrorCode.SYSTEM_ERROR, "Fail to insert article to database. " + article);
        long articleId = article.getId();
        articleFilter.add(articleId);
    }

    @Override
    public Article getArticle(long articleId) {
        return articleStruct.POtoDO(getById(articleId));
    }

    @Override
    public ArticleOps getArticleOps(long userId, long articleId) {
        return baseMapper.getArticleOps(userId, articleId);
    }

    @Override
    public List<Article> getRandPreferArticle(long userId, int size) {
        return loadArticle(baseMapper.getRandPreferArticle(userId, size));
    }

    @Override
    public List<Long> getUniqueArticle(long userId, List<Long> articleIds) {
        return baseMapper.getUniqueArticle(userId, articleIds);
    }

    @Override
    public boolean existArticle(long articleId) {
        return baseMapper.selectCount(
                new QueryWrapper<ArticlePO>()
                        .eq(ArticlePO.Fields.id, articleId)
        ) == 1;
    }

    public boolean addViewHistory(long userId, long articleId, LocalDateTime viewAt) {

        cacheRepo.addLogLogElement(CacheKey.articleViewCount(articleId), userId);

        String historyKey = CacheKey.articleHistorySet(articleId);

        // 若该文章还没有浏览历史，说明也不在近期文章中，则加入近期文章列表
        if(!cacheRepo.exist(historyKey)) {
            cacheRepo.getSortedSet(CacheKey.candidateArticleSet()).add(articleId, 0);
        }

        // 在对应文章的浏览记录中添加一条记录
        BoundZSetOperations<Object, Object> historySet = cacheRepo.getSortedSet(historyKey);

        historySet.add(userId, (double) viewAt.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000 - HotArticleTask.BEGIN_TIMESTAMP);

        return baseMapper.addViewHistory(userId, articleId, viewAt) > 0;
    }

    @Override
    public List<Article> getRecentPreferredArticle(long userId, int size) {
        return loadArticle(baseMapper.getRecentPreferArticle(userId, size));
    }

    @Override
    public List<Article> getRecentViewedArticle(long userId, int size) {
        return loadArticle(baseMapper.getRecentViewedArticle(userId, size));
    }

    @Override
    public List<Article> getUpdatedArticle(LocalDateTime afterTime) {
        return loadArticle(baseMapper.getUpdatedArticle(afterTime));
    }

    @Override
    public List<ArticleHistory> getArticleHistory(long userId, Integer from, Integer size) {
        int offset = from * size;
        return baseMapper.getArticleHistory(userId, offset, size);
    }

    @Override
    public List<Article> getArticleMin(Set<Long> articleIds) {
        return loadArticle(baseMapper.getArticleMin(articleIds));
    }

    @Override
    public boolean hasOwnership(long userId, long articleId) {
        return baseMapper.selectCount(
                new QueryWrapper<ArticlePO>()
                        .eq(ArticlePO.Fields.id, articleId)
                        .eq(ArticlePO.Fields.createBy, userId)
        ) > 0;
    }

    @Override
    public void updateArticle(long userId, Article article) {
        // 删除文章缓存
        cacheRepo.deleteObject(CacheKey.cacheArticle(article.getId()));

        ThrowUtil.ifFalse(update(articleStruct.DOtoPO(article),
                new UpdateWrapper<ArticlePO>().eq(ArticlePO.Fields.id, article.getId()).eq(ArticlePO.Fields.createBy, userId)), "The user has no business to update the article or article does not exists.");
    }

    @Override
    public void updateArticle(Article updatedArticle) {
        ThrowUtil.ifFalse(updateById(articleStruct.DOtoPO(updatedArticle)), "Fail to update article.");
    }

    @Transactional
    @Override
    public void deleteArticle(long userId, long articleId) {

        ThrowUtil.ifFalse(
                remove(
                        new QueryWrapper<ArticlePO>()
                                .eq(ArticlePO.Fields.id, articleId)
                                .eq(ArticlePO.Fields.createBy, userId))
                , "Article does not exists or does not belong to the user.");

        // 删除文章操作记录
        baseMapper.deleteArticleOps(articleId);

        // 删除浏览、点赞、收藏记录
        boolean flag = true;
        flag &= cacheRepo.deleteLogLog(CacheKey.articleViewCount(articleId));
        flag &= cacheRepo.deleteLong(CacheKey.articleFavorCount(articleId));
        flag &= cacheRepo.deleteLong(CacheKey.articleLikeCount(articleId));

        ThrowUtil.ifFalse(flag, ErrorCode.SYSTEM_ERROR, "Fail to delete article relate info in redis.");

        // 删除评论
        commentRepo.deleteComment(ElementType.ARTICLE.getValue(), articleId);
    }

    @Override
    public void markArticle(long userId, long articleId, int mark) {

        // 如果没有不同的评价（即评价已经是mark了），无需修改
        ThrowUtil.ifFalse(baseMapper.hasDiffArticleMark(userId, articleId, mark), ErrorCode.DUPLICATE_REQUEST, "Article has already been marked.");

        CacheIncTask updateMarkTask = new CacheIncTask(mark);
        taskHandler.addTask(CacheKey.articleLikeCount(articleId), updateMarkTask);

        ThrowUtil.ifFalse(baseMapper.markArticle(userId, articleId, mark) > 0, ErrorCode.SYSTEM_ERROR, "Fail to update article mark status in database.");
    }

    @Override
    public void favorArticle(long userId, long articleId, boolean isFavor) {

        // 如果没有不同的收藏状态（即状态已经是isFavor了），无需修改
        ThrowUtil.ifFalse(baseMapper.hasDiffArticleFavor(userId, articleId, isFavor), ErrorCode.DUPLICATE_REQUEST, "Article has already been favored.");

        CacheIncTask updateFavorTask = new CacheIncTask(isFavor ? 1 : -1);
        taskHandler.addTask(CacheKey.articleFavorCount(articleId), updateFavorTask);

        ThrowUtil.ifFalse(baseMapper.favorArticle(userId, articleId, isFavor) > 0, ErrorCode.SYSTEM_ERROR, "Fail to update article favor status in database.");
    }

    private List<Article> loadArticle(List<ArticlePO> articles) {
        return articles.stream().map(articleStruct::POtoDO).collect(Collectors.toList());
    }
}
