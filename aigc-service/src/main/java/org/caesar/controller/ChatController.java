package org.caesar.controller;

import org.caesar.common.context.ContextHolder;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.request.RecommendArticleRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.service.AnalyseTextService;
import org.caesar.service.QuestionHelperService;
import org.caesar.service.RecommendService;
import org.caesar.service.impl.OpenAIChatService;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.response.CompletionResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ChatController {

    @Resource
    private OpenAIChatService chatService;

    @Resource
    private RecommendService recommendService;

    @Resource
    private AnalyseTextService analyseTextService;

    @Resource
    private QuestionHelperService questionHelperService;

    @PostMapping("/completion")
    public Response<CompletionResponse> completion(@RequestBody CompletionRequest request) {
        return Response.ok(chatService.completion(request));
    }

    @PostMapping("/question-helper")
    public Response<QuestionHelperResponse> questionHelper(@RequestBody QuestionHelperRequest request) {
        return Response.ok(questionHelperService.questionHelper(request));
    }

    @PostMapping("/analyse-text")
    public Response<AnalyseTextResponse> analyseContent(@RequestBody AnalyseTextRequest request) {
        return Response.ok(analyseTextService.analyseText(request));
    }

    @GetMapping("/recommend-article")
    public Response<List<ArticleMinVO>> recommendArticle(@RequestParam String userPrompt) {
        long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(recommendService.recommendArticle(userId, userPrompt));
    }

}
