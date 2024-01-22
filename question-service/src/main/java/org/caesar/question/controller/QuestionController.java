package org.caesar.question.controller;

import org.caesar.common.check.CheckManager;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.idempotent.Idempotent;
import org.caesar.domain.common.vo.Response;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.domain.question.response.SubmitCodeResult;
import org.caesar.question.service.QuestionService;
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

        questionService.addQuestion(request);

        return Response.ok(null, "Added question successfully.");
    }

    @DeleteMapping("/{qId}")
    public Response<Void> deleteQuestion(@PathVariable Long qId) {

        ThrowUtil.ifNull(qId, "问题id为空");

        questionService.deleteQuestion(qId);

        return Response.ok(null, "Deleted question successfully.");
    }

    @PutMapping
    public Response<Void> updateQuestion(@RequestBody UpdateQuestionRequest request) {

        CheckManager.checkThrowException(request, UpdateQuestionRequest.class);

        questionService.updateQuestion(request);

        return Response.ok(null, "Updated question successfully.");
    }

    @Idempotent(value = "submitCode", reqId = "#request.submitId", idType = Integer.class,
            successMsg = "提交代码请求已经被处理成功了!",
            processingMsg = "提交代码请求正在处理中，请稍候!")
    @PostMapping("/submit")
    public Response<Void> submitCode(@RequestBody SubmitCodeRequest request) {

        Long userId = ContextHolder.getUserId();

        questionService.submitCode(userId, request);

        return Response.ok();
    }

    @GetMapping("/judge-result")
    public Response<SubmitCodeResult> getJudgeCodeResult(@Min(0) @RequestParam Long qId, @RequestParam Integer submitId) {
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(questionService.getJudgeCodeResult(userId, qId, submitId));
    }

}
