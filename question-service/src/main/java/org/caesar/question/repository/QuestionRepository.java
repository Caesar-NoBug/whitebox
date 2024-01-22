package org.caesar.question.repository;

import org.caesar.domain.question.response.SubmitCodeResult;
import org.caesar.question.model.entity.Question;

import java.time.LocalDateTime;
import java.util.List;

public interface QuestionRepository {

    boolean addQuestion(Question question);

    boolean deleteQuestion(long id);

    boolean updateQuestion(Question question);

    Question getQuestionById(long id);

    List<Question> getUpdatedQuestion(LocalDateTime afterTime);

    SubmitCodeResult getSubmitResult(long userId, long qId, int submitId);

    void addSubmitResult(long userId, long qId, int submitId, SubmitCodeResult submitCodeResult);
}
