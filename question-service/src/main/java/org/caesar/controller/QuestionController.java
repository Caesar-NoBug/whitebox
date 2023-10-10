package org.caesar.controller;


import org.caesar.common.check.CheckManager;
import org.caesar.common.Response;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.model.dto.request.question.AddQuestionRequest;
import org.caesar.common.model.dto.request.question.UpdateQuestionRequest;
import org.caesar.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public Response<Void> addQuestion(@RequestBody AddQuestionRequest request) {

        CheckManager.checkThrowException(request, AddQuestionRequest.class);

        return questionService.addQuestion(request);
    }

    @DeleteMapping("/{qId}")
    public Response<Void> deleteQuestion(@PathVariable Long qId) {

        ThrowUtil.throwIfNull(qId, "问题id为空");

        return questionService.deleteQuestion(qId);
    }

    @PutMapping
    public Response<Void> updateQuestion(@RequestBody UpdateQuestionRequest request) {

        CheckManager.checkThrowException(request, UpdateQuestionRequest.class);

        return questionService.updateQuestion(request);
    }

    /*@PostMapping("/submit")
    public Response<SubmitCodeResponse> submitCode(@RequestBody SubmitCodeRequest request) {

        CheckManager.checkAll(request, SubmitCodeRequest.class);

        return questionService.submitCode(request);
    }*/
}
