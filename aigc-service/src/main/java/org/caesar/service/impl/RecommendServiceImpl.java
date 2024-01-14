package org.caesar.service.impl;

import org.caesar.common.client.ArticleClient;
import org.caesar.common.client.SearchClient;
import org.caesar.common.client.UserClient;
import org.caesar.common.repository.CacheRepository;
import org.caesar.common.str.StrUtil;
import org.caesar.common.resp.RespUtil;
import org.caesar.constant.ChatPrompt;
import org.caesar.constant.RedisKey;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.request.RecommendArticleRequest;
import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.vo.ArticleIndexVO;
import org.caesar.domain.search.vo.SearchHistoryVO;
import org.caesar.domain.user.vo.UserPreferVO;
import org.caesar.service.RecommendService;
import org.caesar.service.ChatService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static final int PREFER_SIZE = 4;

    public static final int SEARCH_HISTORY_SIZE = 5;

    @Override
    public List<ArticleMinVO> recommendArticle(long userId, RecommendArticleRequest request) {

        String cacheKey = RedisKey.CACHE_USER_PROFILE + userId;

        String userProfile = cacheRepo.getObject(cacheKey);

        if(StrUtil.isBlank(userProfile)) {
            userProfile = createUserProfile(cacheKey);
        }

        // 处理用户提示（处理“你是，you are等可能影响gpt的词“）
        String userPrompt = preHandlePrompt(request.getUserPrompt());

        // 筛选候选文章(通过gpt生成的搜索关键词搜出候选文章)
        List<ArticleMinVO> articles = getCandidateArticle(userProfile, userPrompt);

        // 去重，删除用户看过的文章
        List<Long> ids = articles.stream().map(ArticleMinVO::getId).collect(Collectors.toList());
        List<Long> uniqueIds = RespUtil.handleWithThrow(articleClient.getUniqueArticle(ids), "用户文章去重失败");

        articles.removeIf(article -> !uniqueIds.contains(article.getId()));

        // 让gpt筛选出目标文章
        return getRecommendArticle(userProfile, userPrompt, articles);
    }

    private String preHandlePrompt(String userPrompt) {
        return userPrompt.replaceAll("你|you", "");
    }

    private String createUserProfile(String cacheKey) {
        //TODO: 异步优化

        // 获取用户整体偏好
        UserPreferVO userPreferVO = RespUtil.handleWithThrow(
                userClient.getUserPrefer(), "获取用户偏好失败");

        String occupation = userPreferVO.getOccupation();
        String preference = userPreferVO.getPreference();

        // 获取近期偏好文章以及随机获取曾经偏好文章
        GetPreferArticleResponse preferResp = RespUtil.handleWithThrow(
                articleClient.getPreferArticle(PREFER_SIZE, PREFER_SIZE, PREFER_SIZE),
                "获取用户偏好文章失败");

        List<ArticleMinVO> articles = new ArrayList<>();
        articles.addAll(preferResp.getViewedArticles());
        articles.addAll(preferResp.getPreferredArticles());
        articles.addAll(preferResp.getRandPreferredArticles());

        // 处理用户搜索记录(5)
        List<String> searchHistories = RespUtil.handleWithThrow(
                        searchClient.getSearchHistory(SEARCH_HISTORY_SIZE),
                        "获取用户搜索记录失败").stream()
                .map(SearchHistoryVO::getContent).collect(Collectors.toList());

        CompletionRequest chatRequest = new CompletionRequest();
        chatRequest.setPreset(ChatPrompt.PRESET_CREATE_USER_PROFILE);
        chatRequest.setPrompt(ChatPrompt.createUserProfilePrompt(occupation, preference, articles, searchHistories));
        chatRequest.setHighestTemperature();

        String userProfile = chatService.completion(chatRequest).getReply();

        // 缓存用户画像12个小时
        cacheRepo.setObject(cacheKey, userProfile, 12, TimeUnit.HOURS);

        return userProfile;
    }

    private List<ArticleMinVO> getCandidateArticle(String userProfile, String userPrompt) {
        CompletionRequest request = new CompletionRequest();
        request.setHighestTemperature();
        request.setPreset(ChatPrompt.PRESET_CREATE_CANDIDATE_ARTICLE);
        request.setPrompt(ChatPrompt.createCandidateArticle(userProfile, userPrompt));
        String reply = chatService.completion(request).getReply();
        String[] candidates = reply.split("\n");
        // 把candidates转换成list

        List<ArticleIndexVO> candidateArticles = RespUtil.handleWithThrow(
                searchClient.searchBatch(Arrays.asList(candidates), 5, DataSource.ARTICLE),
                "查询候选文章失败"
        );

        return candidateArticles.stream().map(ArticleMinVO::new).collect(Collectors.toList());
    }

    private List<ArticleMinVO> getRecommendArticle(String userProfile, String userPrompt, List<ArticleMinVO> candidates) {
        //TODO: 指定推荐文章的数量
        CompletionRequest request = new CompletionRequest();
        request.setHighestTemperature();
        request.setPreset(ChatPrompt.PRESET_RECOMMEND_ARTICLE);
        request.setPrompt(ChatPrompt.recommendArticlePrompt(userProfile, userPrompt, candidates));

        List<Long> ids = Arrays
                .stream(
                        chatService.completion(request).getReply().split(",")
                )
                .map(Long::parseLong).collect(Collectors.toList());

        List<ArticleMinVO> recommendArticles = new ArrayList<>();

        candidates.forEach(article -> {
            if(ids.contains(article.getId()))
                recommendArticles.add(article);
        });

        return recommendArticles;
    }

}
