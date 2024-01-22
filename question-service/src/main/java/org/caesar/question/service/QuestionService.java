package org.caesar.question.service;

import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.domain.question.response.SubmitCodeResult;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Service
 * @createDate 2023-08-30 10:04:07
 */
public interface QuestionService {
    //TODO: 不要返回Response
    void addQuestion(AddQuestionRequest request);

    void deleteQuestion(Long qId);

    void updateQuestion(UpdateQuestionRequest request);

    void submitCode(long userId, SubmitCodeRequest request);

    void judgeCode(long userId, ExecuteCodeResponse response);

    SubmitCodeResult getJudgeCodeResult(long userId, long qId, int submitId);
}

