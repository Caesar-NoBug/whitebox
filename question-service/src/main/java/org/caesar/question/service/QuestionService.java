package org.caesar.question.service;

import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.domain.question.response.SubmitCodeResult;
import org.caesar.domain.question.vo.QuestionVO;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Service
 * @createDate 2023-08-30 10:04:07
 */
public interface QuestionService {

    // 基本增删改查
    void addQuestion(AddQuestionRequest request);

    void deleteQuestion(long questionId);

    void updateQuestion(UpdateQuestionRequest request);

    QuestionVO getQuestionVO(long userId, long questionId);

    // 问题助手: 给用户错误代码修正提示
    QuestionHelperResponse questionHelper(long userId, long questionId, int submitId);

    // 评价问题(-1:踩，0:无，1:赞)
    void markQuestion(long userId, long questionId, int mark);

    // 收藏问题
    void favorQuestion(long userId, long questionId, boolean isFavor);

    // 提交代码
    void submitCode(long userId, SubmitCodeRequest request);

    // 判断代码是否正确
    void judgeCode(long userId, ExecuteCodeResponse response);

    // 获取代码判断结果
    SubmitCodeResult getJudgeCodeResult(long userId, long qId, int submitId);
}

