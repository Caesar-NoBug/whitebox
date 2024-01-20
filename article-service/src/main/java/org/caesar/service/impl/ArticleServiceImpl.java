package org.caesar.service.impl;

import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.model.entity.ArticleHistory;
import org.caesar.util.RedisKey;
import org.caesar.common.client.AIGCClient;
import org.caesar.common.client.UserClient;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.repository.CacheRepository;
import org.caesar.common.resp.RespUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.caesar.domain.article.request.AddArticleRequest;
import org.caesar.domain.article.request.UpdateArticleRequest;
import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.model.MsArticleStruct;
import org.caesar.model.entity.Article;
import org.caesar.model.entity.ArticleOps;
import org.caesar.repository.ArticleRepository;
import org.caesar.service.ArticleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class ArticleServiceImpl implements ArticleService {

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
    //TODO: XSS检测

    //TODO: 把文章的数据定时同步到MySQL
    //TODO: 定时更新文章浏览数、点赞数、收藏数（通过hyperLogLog）
    @Override
    public void addArticle(long userId, AddArticleRequest request) {
        long id = cacheRepo.nextId(RedisKey.articleIncId());
        Article article = Article.fromAddRequest(id, userId, request);
        boolean genContent = request.isGenContent();

        // 进行文章审核和智能分析
        Response<AnalyseTextResponse> analyseResp = aigcClient.analyseText(new AnalyseTextRequest(article.getTitle(), article.getContent(), genContent));

        AnalyseTextResponse response = RespUtil.handleWithThrow(analyseResp, "Fail to analyse the article");

        ThrowUtil.ifFalse(response.isPass(), "The article did not pass the review");

        if (genContent) {
            article.setTag(response.getTags());
            article.setDigest(response.getDigest());
        }

        ThrowUtil.ifFalse(articleRepo.addArticle(article), ErrorCode.SYSTEM_ERROR, "fail to add the article");
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
    public List<ArticleMinVO> getHotArticle() {
        return cacheRepo.getObject(RedisKey.hotArticleSet());
    }

    @Override
    public GetPreferArticleResponse getPreferArticle(long userId, int viewedSize, int preferredSize, int randPreferredSize) {

        GetPreferArticleResponse response = new GetPreferArticleResponse();

        response.setViewedArticles(
                loadArticleMinVO(articleRepo.getRecentViewedArticle(userId, viewedSize))
        );

        response.setPreferredArticles(
                loadArticleMinVO(articleRepo.getRecentPreferredArticle(userId, preferredSize))
        );

        response.setRandPreferredArticles(
                loadArticleMinVO(articleRepo.getRandPreferArticle(userId, randPreferredSize))
        );

        return response;
    }

    @Override
    public List<ArticleMinVO> getArticleMin(Set<Long> articleIds) {

        ThrowUtil.ifEmpty(articleIds, ErrorCode.SYSTEM_ERROR, "id集合为空");

        return articleRepo.getArticleMin(articleIds)
                .stream().map(articleStruct::DOtoMinVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleHistoryVO> getArticleHistory(long userId, Integer from, Integer size) {
        List<ArticleHistory> histories = articleRepo.getArticleHistory(userId, from, size);
        List<Long> authorIds = histories.stream().map(ArticleHistory::getCreateBy).collect(Collectors.toList());

        Map<Long, UserMinVO> authorInfo = RespUtil.handleWithThrow(
                userClient.getUserMin(authorIds),
                "获取作者信息失败"
        );

        ThrowUtil.ifEmpty(authorInfo, ErrorCode.SYSTEM_ERROR, "获取作者信息失败,作者不存在");

        List<ArticleHistoryVO> historyVOs = new ArrayList<>();
        for (ArticleHistory history : histories) {
            ArticleHistoryVO historyVO = articleStruct.DOtoHistoryVO(history);
            historyVO.setAuthor(authorInfo.get(history.getCreateBy()));
            historyVOs.add(historyVO);
        }

        return historyVOs;
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
        ThrowUtil.ifFalse(articleRepo.existArticle(articleId), ErrorCode.INVALID_ARGS_ERROR, "文章不存在");
        ThrowUtil.ifFalse(articleRepo.markArticle(userId, articleId, mark), ErrorCode.SYSTEM_ERROR, "");
    }

    @Override
    public void favorArticle(long userId, long articleId, boolean isFavor) {
        ThrowUtil.ifFalse(articleRepo.existArticle(articleId), ErrorCode.INVALID_ARGS_ERROR, "文章不存在");
        ThrowUtil.ifFalse(articleRepo.favorArticle(userId, articleId, isFavor), ErrorCode.SYSTEM_ERROR, "");
    }

    @Override
    public List<Long> getUniqueArticle(long userId, List<Long> articleIds) {
        return articleRepo.getUniqueArticle(userId, articleIds);
    }


    /**
     * 从缓存中或数据库中获取文章数据，并缓存文章
     *
     * @param articleId 文章id
     * @return 文章数据
     */
    private ArticleVO loadArticleVO(long articleId) {
        //TODO: 用布隆过滤器预防缓存穿透
        String cacheKey = RedisKey.cacheArticle(articleId);
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

        Map<Long, UserMinVO> authorInfo = RespUtil.handleWithThrow(
                userClient.getUserMin(Collections.singletonList(authorId)), "获取作者信息失败"
        );

        ThrowUtil.ifEmpty(authorInfo, ErrorCode.SYSTEM_ERROR, "获取作者信息失败,作者不存在");

        articleVO.setAuthor(authorInfo.get(authorId));

        int expire = (int) (5 + (Math.random() * 10));
        cacheRepo.setObject(cacheKey, articleVO, expire, TimeUnit.MINUTES);

        return articleVO;
    }

    private List<ArticleMinVO> loadArticleMinVO(List<Article> articles) {
        return articles.stream().map(articleStruct::DOtoMinVO).collect(Collectors.toList());
    }

}
