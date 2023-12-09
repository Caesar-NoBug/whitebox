package org.caesar.controller;

import org.caesar.domain.aigc.request.AnalyseContentRequest;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.AnalyseContentResponse;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.service.impl.OpenAIChatService;
import org.caesar.common.vo.Response;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.response.CompletionResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ChatController {

    @Resource
    private OpenAIChatService chatService;

    @PostMapping("/completion")
    public Response<CompletionResponse> completion(@RequestBody CompletionRequest request) {
        return Response.ok(chatService.completion(request));
    }

    @PostMapping("/problem-helper")
    public Response<QuestionHelperResponse> solveProblem(@RequestBody QuestionHelperRequest request) {
        return Response.ok(chatService.questionHelper(request));
    }

    @PostMapping("/analyse-content")
    public Response<AnalyseContentResponse> analyseContent(@RequestBody AnalyseContentRequest request) {
        return Response.ok(chatService.analyseContent(request));
    }

    @PostMapping("/assistant")
    public Response<CompletionResponse> summary(@RequestBody CompletionRequest request) {
        return Response.ok(chatService.assistant(request));
    }

}
