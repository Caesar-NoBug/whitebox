package org.caesar.common.client;

import org.caesar.common.client.fallback.AIGCClientFallback;
import org.caesar.common.log.Logger;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.request.RecommendArticleRequest;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "aigc-service", fallback = AIGCClientFallback.class)
public interface AIGCClient {

    @Logger(value = "[RPC] /analyseText", args = true, result = true)
    @PostMapping("/analyse-text")
    Response<AnalyseTextResponse> analyseText(@RequestBody AnalyseTextRequest request);

    @Logger(value = "[RPC] /recommendArticle", args = true, result = true)
    @GetMapping("/recommend-article")
    Response<List<ArticleMinVO>> recommendArticle(@RequestParam String userPrompt);


    @Logger(value = "[RPC] /questionHelper", args = true, result = true)
    @PostMapping("/question-helper")
    Response<QuestionHelperResponse> questionHelper(@RequestBody QuestionHelperRequest request);
}
