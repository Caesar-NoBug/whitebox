package org.caesar.service;

import org.caesar.domain.common.vo.Response;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.JudgeCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
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
    //TODO: 不要返回Response
    Response<Void> addQuestion(AddQuestionRequest request);

    Response<Void> deleteQuestion(Long qId);

    Response<Void> updateQuestion(UpdateQuestionRequest request);

    Response<String> judgeCode(JudgeCodeRequest request);

    @Async
    void doJudgeCode(String submitId, JudgeCodeRequest request, Question question);
}

