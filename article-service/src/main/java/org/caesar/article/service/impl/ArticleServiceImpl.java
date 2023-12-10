package org.caesar.article.service.impl;

import org.caesar.common.client.AIGCClient;
import org.caesar.common.client.UserClient;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.repository.CacheRepository;
import org.caesar.common.util.ClientUtil;
import org.caesar.common.vo.Response;
import org.caesar.article.constant.RedisPrefix;
import org.caesar.domain.aigc.request.AnalyseContentRequest;
import org.caesar.domain.aigc.response.AnalyseContentResponse;
import org.caesar.domain.article.request.AddArticleRequest;
import org.caesar.domain.article.request.UpdateArticleRequest;
import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.article.model.MsArticleStruct;
import org.caesar.article.model.entity.Article;
import org.caesar.article.model.entity.ArticleOps;
import org.caesar.article.repository.ArticleRepository;
import org.caesar.article.service.ArticleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class ArticleServiceImpl implements ArticleService{

    @Resource
    private ArticleRepository articleRepo;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private MsArticleStruct articleStruct;

    @Resource
    private AIGCClient aigcClient;

    @Resource
    private UserClient userClient;
    //TODO: 定时更新文章浏览数、点赞数、收藏数（通过hyperLogLog）
    @Override
    public void addArticle(long userId, AddArticleRequest request) {
        long id = cacheRepo.nextId(RedisPrefix.ARTICLE_INC_ID);
        Article article = Article.fromAddRequest(id, userId, request);
        boolean genContent = request.isGenContent();
        Response<AnalyseContentResponse> analyseResp = aigcClient.analyseContent(new AnalyseContentRequest(article.getTitle(), article.getContent(), genContent));

        AnalyseContentResponse response = ClientUtil.handleResponse(analyseResp, "审核文章/分析文章失败");

        if(genContent) {
            article.setTags(response.getTags());
            article.setDigest(response.getDigest());
        }

        ThrowUtil.ifFalse(articleRepo.addArticle(article), ErrorCode.SYSTEM_ERROR, "无法添加文章");
    }

    @Override
    public ArticleVO viewArticle(long userId, long articleId) {

        // 获取文章信息
        ArticleVO articleVO = loadArticleVO(articleId);

        // 添加浏览记录
        articleRepo.addViewHistory(userId, articleId, LocalDateTime.now());

        // 获取用户对文章的点赞、收藏状态
        ArticleOps ops = articleRepo.getArticleOps(userId, articleId);
        articleVO.setFavored(ops.isFavored());
        articleVO.setMark(ops.getMark());

        return articleVO;
    }

    @Override
    public List<Article> getUpdatedArticle(LocalDateTime afterTime) {
        return articleRepo.getUpdatedArticle(afterTime);
    }

    @Override
    public List<ArticleHistoryVO> getArticleHistory(long userId, Integer from, Integer size) {
        List<Article> articles = articleRepo.getArticleHistory(userId, from, size);
        List<Long> authorIds = articles.stream().map(Article::getId).collect(Collectors.toList());

        Map<Long, UserMinVO> authorInfo = ClientUtil.handleResponse(
                userClient.getUserMin(authorIds),
                "获取作者信息失败"
        );

        ThrowUtil.ifEmpty(authorInfo, ErrorCode.SYSTEM_ERROR, "获取作者信息失败,作者不存在");

        List<ArticleHistoryVO> histories = new ArrayList<>();
        for (Article article : articles) {
            ArticleHistoryVO historyVO = articleStruct.DOtoHistoryVO(article);
            historyVO.setAuthor(authorInfo.get(article.getCreateBy()));
            histories.add(historyVO);
        }

        return histories;
    }

    @Override
    public void updateArticle(long userId, UpdateArticleRequest request) {
        ThrowUtil.ifFalse(articleRepo.updateArticle(userId, Article.fromUpdateRequest(request)), ErrorCode.NOT_AUTHORIZED_ERROR, "无权限修改文章");
    }

    @Override
    public void deleteArticle(long userId, long articleId) {
        ThrowUtil.ifFalse(articleRepo.deleteArticle(userId, articleId), ErrorCode.NOT_AUTHORIZED_ERROR, "无权限删除文章");
    }

    @Override
    public void markArticle(long userId, long articleId, int mark) {
        ThrowUtil.ifFalse(articleRepo.existArticle(articleId), ErrorCode.ILLEGAL_PARAM_ERROR, "文章不存在");
        ThrowUtil.ifFalse(articleRepo.markArticle(userId, articleId, mark), ErrorCode.SYSTEM_ERROR, "");
    }

    @Override
    public void favorArticle(long userId, long articleId, boolean isFavor) {
        ThrowUtil.ifFalse(articleRepo.existArticle(articleId), ErrorCode.ILLEGAL_PARAM_ERROR, "文章不存在");
        ThrowUtil.ifFalse(articleRepo.favorArticle(userId, articleId, isFavor), ErrorCode.SYSTEM_ERROR, "");
    }

    /**
     * 从缓存中或数据库中获取文章数据，并缓存文章
     * @param articleId 文章id
     * @return  文章数据
     */
    private ArticleVO loadArticleVO(long articleId) {

        String cacheKey = RedisPrefix.CACHE_ARTICLE + articleId;
        // 尝试从缓存中获取文章
        ArticleVO articleVO = cacheRepo.getObject(cacheKey);

        if (articleVO != null) {
            // 热点数据持续刷新过期时间
            cacheRepo.expire(cacheKey, 20, TimeUnit.MINUTES);
            return articleVO;
        }

        Article article = articleRepo.getArticle(articleId);

        ThrowUtil.ifNull(article, "文章不存在");

        articleVO = articleStruct.DOtoVO(article);

        // 获取作者信息
        Long authorId = article.getCreateBy();

        Map<Long, UserMinVO> authorInfo = ClientUtil.handleResponse(
                userClient.getUserMin(Collections.singletonList(authorId)), "获取作者信息失败"
        );

        ThrowUtil.ifEmpty(authorInfo, ErrorCode.SYSTEM_ERROR, "获取作者信息失败,作者不存在");

        articleVO.setAuthor(authorInfo.get(authorId));

        int expire = (int) (5 + (Math.random() * 10));
        cacheRepo.setObject(cacheKey, articleVO, expire, TimeUnit.MINUTES);

        return articleVO;
    }

}
