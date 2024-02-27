package org.caesar.question.service;

import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.domain.question.vo.QuestionVO;
import org.caesar.question.model.entity.Question;

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

    Question getCacheQuestion(long questionId);

    // 评价问题(-1:踩，0:无，1:赞)
    void markQuestion(long userId, long questionId, int mark);

    // 收藏问题
    void favorQuestion(long userId, long questionId, boolean isFavor);
}

