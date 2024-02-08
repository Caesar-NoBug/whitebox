package org.caesar.question.controller;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.idempotent.Idempotent;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.domain.question.response.SubmitCodeResult;
import org.caesar.question.service.QuestionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Validated
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @PostMapping
    public Response<Void> addQuestion(@Valid @RequestBody AddQuestionRequest request) {

        questionService.addQuestion(request);

        return Response.ok(null, "Added question successfully.");
    }

    @DeleteMapping("/{questionId}")
    public Response<Void> deleteQuestion(@NotNull @PathVariable Long questionId) {

        questionService.deleteQuestion(questionId);

        return Response.ok(null, "Deleted question successfully.");
    }

    @PutMapping
    public Response<Void> updateQuestion(@Valid @RequestBody UpdateQuestionRequest request) {
        questionService.updateQuestion(request);

        return Response.ok(null, "Updated question successfully.");
    }

    @Idempotent(value = "question:submit", reqId = "#request.submitId",
            successMsg = "提交代码请求已经被处理成功了!",
            processingMsg = "提交代码请求正在处理中，请稍候!")
    @PostMapping("/submit")
    public Response<Void> submitCode(@Valid @RequestBody SubmitCodeRequest request) {

        Long userId = ContextHolder.getUserId();

        questionService.submitCode(userId, request);

        return Response.ok();
    }

    @GetMapping("/judge-result")
    public Response<SubmitCodeResult> getJudgeCodeResult(@Min(0) @RequestParam Long questionId, @NotNull @RequestParam Integer submitId) {
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(questionService.getJudgeCodeResult(userId, questionId, submitId));
    }

    @GetMapping("/question-helper")
    public Response<QuestionHelperResponse> questionHelper(@Min(0) @RequestParam Long questionId,@NotNull @RequestParam Integer submitId) {
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(questionService.questionHelper(userId, questionId, submitId));
    }
}
