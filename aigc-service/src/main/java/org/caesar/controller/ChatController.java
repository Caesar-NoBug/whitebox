package org.caesar.controller;

import org.caesar.common.context.ContextHolder;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.request.RecommendArticleRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.service.AnalyseService;
import org.caesar.service.RecommendService;
import org.caesar.service.impl.OpenAIChatService;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.response.CompletionResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ChatController {

    @Resource
    private OpenAIChatService chatService;

    @Resource
    private RecommendService recommendService;

    @Resource
    private AnalyseService analyseService;

    @PostMapping("/completion")
    public Response<CompletionResponse> completion(@RequestBody CompletionRequest request) {
        return Response.ok(chatService.completion(request));
    }

    @PostMapping("/problem-helper")
    public Response<QuestionHelperResponse> solveProblem(@RequestBody QuestionHelperRequest request) {
        return Response.ok(chatService.questionHelper(request));
    }

    @PostMapping("/analyse-text")
    public Response<AnalyseTextResponse> analyseContent(@RequestBody AnalyseTextRequest request) {
        return Response.ok(analyseService.analyseText(request));
    }

    @PostMapping("/assistant")
    public Response<CompletionResponse> summary(@RequestBody CompletionRequest request) {
        return Response.ok(chatService.assistant(request));
    }

    @PostMapping("/recommend-article")
    public Response<List<ArticleMinVO>> recommendArticle(@RequestBody RecommendArticleRequest request) {
        long userId = ContextHolder.getUserId();
        return Response.ok(recommendService.recommendArticle(userId, request));
    }

}
