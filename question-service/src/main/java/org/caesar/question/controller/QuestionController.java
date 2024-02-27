package org.caesar.question.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.idempotent.Idempotent;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.domain.question.vo.SubmitCodeResultVO;
import org.caesar.domain.question.vo.QuestionVO;
import org.caesar.domain.search.vo.PageVO;
import org.caesar.question.service.JudgeService;
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
@Api(tags = "问题服务")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private JudgeService judgeService;

    @ApiOperation("新增问题")
    @PostMapping("/ops")
    public Response<Void> addQuestion(@Valid @RequestBody AddQuestionRequest request) {

        questionService.addQuestion(request);

        return Response.ok(null, "Added question successfully.");
    }

    @ApiOperation("删除问题")
    @DeleteMapping("/ops/{questionId}")
    public Response<Void> deleteQuestion(@NotNull @PathVariable Long questionId) {

        questionService.deleteQuestion(questionId);

        return Response.ok(null, "Deleted question successfully.");
    }

    @ApiOperation("修改问题")
    @PutMapping("/ops")
    public Response<Void> updateQuestion(@Valid @RequestBody UpdateQuestionRequest request) {
        questionService.updateQuestion(request);

        return Response.ok(null, "Updated question successfully.");
    }

    @ApiOperation("查看问题")
    @GetMapping("/view/{questionId}")
    public Response<QuestionVO> getQuestion(@PathVariable Long questionId) {
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(questionService.getQuestionVO(userId, questionId));
    }

    @ApiOperation("提交代码")
    @Idempotent(value = "question:submit", reqId = "#request.submitId",
            successMsg = "提交代码请求已经被处理成功了!",
            processingMsg = "提交代码请求正在处理中，请稍候!")
    @PostMapping("/submit")
    public Response<Void> submitCode(@Valid @RequestBody SubmitCodeRequest request) {

        Long userId = ContextHolder.getUserId();

        judgeService.submitCode(userId, request);

        return Response.ok();
    }

    @ApiOperation("获取提交结果")
    @GetMapping("/judge-result")
    public Response<SubmitCodeResultVO> getJudgeCodeResult(@Min(0) @RequestParam Long questionId, @NotNull @RequestParam Integer submitId) {
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(judgeService.getJudgeCodeResult(userId, questionId, submitId));
    }

    @ApiOperation("获取提交结果列表")
    @GetMapping("/judge-result/list")
    public Response<PageVO<SubmitCodeResultVO>> getJudgeCodeResult(@Min(0) @RequestParam Long questionId,
                                                                   @Min(0) @RequestParam Integer from,
                                                                   @Min(0) @RequestParam Integer size) {
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(judgeService.listSubmitResult(userId, questionId, from, size));
    }

    @ApiOperation("问题助手")
    @GetMapping("/question-helper")
    public Response<QuestionHelperResponse> questionHelper(@Min(0) @RequestParam Long questionId,@NotNull @RequestParam Integer submitId) {
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(judgeService.questionHelper(userId, questionId, submitId));
    }
}
