package org.caesar.repository;

import org.caesar.domain.question.response.JudgeCodeResponse;
import org.caesar.model.entity.Question;

import java.time.LocalDateTime;
import java.util.List;

public interface QuestionRepository {

    boolean addQuestion(Question question);

    boolean deleteQuestion(long id);

    boolean updateQuestion(Question question);

    Question getQuestionById(long id);

    List<Question> getUpdatedQuestion(LocalDateTime afterTime);

    JudgeCodeResponse getJudgeResult(long qId, int submitId);
}
