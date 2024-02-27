package org.caesar.question.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.caesar.question.model.entity.Question;
import org.caesar.question.model.entity.QuestionOps;
import org.caesar.question.model.entity.SubmitCodeResult;

import java.time.LocalDateTime;
import java.util.List;

public interface QuestionRepository {

    // 问题增删改查
    void addQuestion(Question question);

    void deleteQuestion(long id);

    void updateQuestion(Question question);

    Question getQuestionById(long id);

    // 问题操作相关
    // 获取用户对文章的操作（点赞、点踩、收藏）
    QuestionOps getQuestionOps(long userId, long questionId);

    // 评价问题(-1:踩，0:无，1:赞)
    void markQuestion(long userId, long questionId, int mark);

    // 收藏问题
    void favorQuestion(long userId, long questionId, boolean isFavor);

    List<Question> getUpdatedQuestion(LocalDateTime afterTime);

    SubmitCodeResult getSubmitResult(long userId, long qId, int submitId);

    // 获取提交结果（不包含详细信息）
    Page<SubmitCodeResult> listSubmitResult(long userId, long questionId, int from, int size);

    void addSubmitResult(SubmitCodeResult submitCodeResult);

    void updateSubmitResult(SubmitCodeResult submitCodeResult);
}
