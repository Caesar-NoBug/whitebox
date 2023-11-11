package org.caesar.service;

import org.caesar.common.Response;
import org.caesar.domain.response.question.JudgeCodeResponse;
import org.caesar.domain.request.question.AddQuestionRequest;
import org.caesar.domain.request.question.JudgeCodeRequest;
import org.caesar.domain.request.question.UpdateQuestionRequest;
import org.caesar.model.entity.Question;
import org.caesar.model.po.QuestionPO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.Async;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Service
 * @createDate 2023-08-30 10:04:07
 */
public interface QuestionService extends IService<QuestionPO> {
    Response<Void> addQuestion(AddQuestionRequest request);

    Response<Void> deleteQuestion(Long qId);

    Response<Void> updateQuestion(UpdateQuestionRequest request);

    Response<String> judgeCode(JudgeCodeRequest request);

    @Async
    void doJudgeCode(String submitId, JudgeCodeRequest request, Question question);
}

