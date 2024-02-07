package org.caesar.service.impl;

import org.caesar.common.client.ArticleClient;
import org.caesar.common.client.SearchClient;
import org.caesar.common.client.UserClient;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.str.StrUtil;
import org.caesar.common.resp.RespUtil;
import org.caesar.config.ChatConfig;
import org.caesar.config.ChatProperties;
import org.caesar.constant.RedisKey;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.request.RecommendArticleRequest;
import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.vo.ArticleIndexVO;
import org.caesar.domain.search.vo.SearchHistoryVO;
import org.caesar.domain.user.vo.UserPreferVO;
import org.caesar.service.RecommendService;
import org.caesar.service.ChatService;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Resource
    private ChatService chatService;

    @Resource
    private UserClient userClient;

    @Resource
    private ArticleClient articleClient;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private SearchClient searchClient;

    @Resource
    private TaskExecutor taskExecutor;

    public static final int PREFER_SIZE = 4;

    public static final int SEARCH_HISTORY_SIZE = 5;

    private ChatConfig userProfileConfig;

    private ChatConfig candidateArticleConfig;

    private ChatConfig selectArticleConfig;

    public RecommendServiceImpl(ChatProperties chatProperties) {
        userProfileConfig = chatProperties.getChatConfig(ChatProperties.RECOMMEND_USER_PROFILE);
        candidateArticleConfig = chatProperties.getChatConfig(ChatProperties.RECOMMEND_CANDIDATE_ARTICLE);
        selectArticleConfig = chatProperties.getChatConfig(ChatProperties.RECOMMEND_SELECT_ARTICLE);
    }

    @Override
    public List<ArticleMinVO> recommendArticle(long userId, RecommendArticleRequest request) {

        String cacheKey = RedisKey.CACHE_USER_PROFILE + userId;

        String userProfile = cacheRepo.getObject(cacheKey);

        if (StrUtil.isBlank(userProfile)) {
            userProfile = createUserProfile(cacheKey);
        }

        // 处理用户提示（处理“你是，you are等可能影响gpt的词“）
        String userPrompt = preHandlePrompt(request.getUserPrompt());

        // 筛选候选文章(通过gpt生成的搜索关键词搜出候选文章)
        List<ArticleMinVO> articles = getCandidateArticle(userProfile, userPrompt);

        // 去重，删除用户看过的文章
        List<Long> ids = articles.stream().map(ArticleMinVO::getId).collect(Collectors.toList());
        List<Long> uniqueIds = RespUtil.handleWithThrow(articleClient.getUniqueArticle(ids), "Fail to fetch unread article from article service.");

        articles.removeIf(article -> !uniqueIds.contains(article.getId()));

        // 让gpt筛选出目标文章
        return selectArticle(userProfile, userPrompt, articles);
    }

    private String preHandlePrompt(String userPrompt) {
        return userPrompt.replaceAll("你|you", "");
    }

    private String createUserProfile(String cacheKey) {

        // 获取用户整体偏好
        CompletableFuture<UserPreferVO> userPreferFuture = CompletableFuture.supplyAsync(
                () -> RespUtil.handleWithThrow(userClient.getUserPrefer(),
                        "Fail to get user preferences."), taskExecutor);

        // 获取近期偏好文章以及随机获取曾经偏好文章
        CompletableFuture<GetPreferArticleResponse> preferArticleFuture = CompletableFuture.supplyAsync(
                () -> RespUtil.handleWithThrow(articleClient.getPreferArticle(PREFER_SIZE, PREFER_SIZE, PREFER_SIZE),
                        "Fail to get user preferred articles."), taskExecutor);

        // 处理用户搜索记录(5)
        CompletableFuture<List<SearchHistoryVO>> searchHistoryFuture = CompletableFuture.supplyAsync(() -> RespUtil.handleWithThrow(
                        searchClient.getSearchHistory(SEARCH_HISTORY_SIZE, DataSource.ARTICLE),
                        "Fail to get user's search history.")
                , taskExecutor);

        UserPreferVO userPreferVO;
        GetPreferArticleResponse preferArticle;
        List<String> searchHistory;

        try {
            userPreferVO = userPreferFuture.get();
            preferArticle = preferArticleFuture.get();
            searchHistory = searchHistoryFuture.get().stream().map(SearchHistoryVO::getContent).collect(Collectors.toList());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Fail to fetch analyze info.", e);
        }

        // 解析用户偏好
        String occupation = userPreferVO.getOccupation();
        String preference = userPreferVO.getPreference();

        // 解析用户近期偏好的文章
        List<ArticleMinVO> articles = new ArrayList<>();
        articles.addAll(preferArticle.getViewedArticles());
        articles.addAll(preferArticle.getPreferredArticles());
        articles.addAll(preferArticle.getRandPreferredArticles());

        CompletionRequest chatRequest = new CompletionRequest();
        chatRequest.setPreset(userProfileConfig.getPreset());
        chatRequest.setPrompt(String.format(userProfileConfig.getPrompt(), occupation, preference, articles, searchHistory));
        chatRequest.setHighestTemperature();

        // 创建用户画像
        String userProfile = chatService.completion(chatRequest).getReply();

        // 缓存用户画像12个小时
        cacheRepo.setObject(cacheKey, userProfile, 12, TimeUnit.HOURS);

        return userProfile;
    }

    private List<ArticleMinVO> getCandidateArticle(String userProfile, String userPrompt) {
        CompletionRequest request = new CompletionRequest();
        request.setHighestTemperature();
        request.setPreset(candidateArticleConfig.getPreset());
        request.setPrompt(String.format(candidateArticleConfig.getPrompt(), userProfile, userPrompt));
        String reply = chatService.completion(request).getReply();
        String[] candidates = reply.split("\n");
        // 把candidates转换成list

        List<ArticleIndexVO> candidateArticles = RespUtil.handleWithThrow(
                searchClient.searchBatch(Arrays.asList(candidates), 5, DataSource.ARTICLE),
                "Fail to search the candidate article."
        );

        return candidateArticles.stream().map(ArticleMinVO::new).collect(Collectors.toList());
    }

    private List<ArticleMinVO> selectArticle(String userProfile, String userPrompt, List<ArticleMinVO> candidates) {
        //TODO: 指定推荐文章的数量
        CompletionRequest request = new CompletionRequest();
        request.setHighestTemperature();
        request.setPreset(selectArticleConfig.getPreset());
        request.setPrompt(String.format(selectArticleConfig.getPrompt(), userProfile, userPrompt, candidates));

        List<Long> ids = Arrays
                .stream(
                        chatService.completion(request).getReply().split(",")
                )
                .map(Long::parseLong).collect(Collectors.toList());

        List<ArticleMinVO> recommendArticles = new ArrayList<>();

        candidates.forEach(article -> {
            if (ids.contains(article.getId()))
                recommendArticles.add(article);
        });

        return recommendArticles;
    }

}
