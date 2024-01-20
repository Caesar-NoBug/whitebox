package org.caesar.controller;

import org.caesar.common.check.CheckManager;
import org.caesar.domain.common.vo.Response;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.JudgeCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.domain.question.response.JudgeCodeResponse;
import org.caesar.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @PostMapping
    public Response<Void> addQuestion(@RequestBody AddQuestionRequest request) {

        CheckManager.checkThrowException(request, AddQuestionRequest.class);

        return questionService.addQuestion(request);
    }

    @DeleteMapping("/{qId}")
    public Response<Void> deleteQuestion(@PathVariable Long qId) {

        ThrowUtil.ifNull(qId, "问题id为空");

        return questionService.deleteQuestion(qId);
    }

    @PutMapping
    public Response<Void> updateQuestion(@RequestBody UpdateQuestionRequest request) {

        CheckManager.checkThrowException(request, UpdateQuestionRequest.class);

        return questionService.updateQuestion(request);
    }

    @PostMapping("/submit")
    public Response<String> submitCode(@RequestBody JudgeCodeRequest request) {

        CheckManager.checkThrowException(request, JudgeCodeRequest.class);

        return questionService.judgeCode(request);
    }

    @GetMapping("result/{id}")
    public Response<JudgeCodeResponse> getSubmitResult(@Min(0) @PathVariable Long id) {
        return null;
    }

}
