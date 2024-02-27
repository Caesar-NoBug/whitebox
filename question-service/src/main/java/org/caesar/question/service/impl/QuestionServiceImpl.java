package org.caesar.question.service.impl;

import org.caesar.common.cache.CacheRepository;
import org.caesar.common.util.DataFilter;
import org.caesar.domain.question.vo.QuestionVO;
import org.caesar.question.constant.CacheKey;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.question.judge.QuestionJudgeManager;
import org.caesar.question.model.MsQuestionStruct;
import org.caesar.question.model.entity.Question;
import org.caesar.question.model.entity.QuestionOps;
import org.caesar.question.repository.QuestionRepository;
import org.caesar.question.service.QuestionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;


@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionRepository questionRepo;

    @Resource
    private MsQuestionStruct questionStruct;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private QuestionJudgeManager judgeManager;

    @Resource
    private DataFilter<Long> questionFilter;

    @Override
    public void addQuestion(AddQuestionRequest request) {

        long questionId = cacheRepo.nextId(CacheKey.QUESTION_INC_ID);

        LocalDateTime now = LocalDateTime.now();

        Question question = new Question(judgeManager, questionId, request.getTitle(), request.getContent(),
                request.getInputCase(), request.getOutputCase(), request.getQType(),
                request.getTag(), request.getDifficulty(), 0, 0,
                0, request.getTimeLimit(), request.getMemoryLimit(), 0, now, now);

        questionRepo.addQuestion(question);

        questionFilter.add(questionId);
    }

    @Override
    public void deleteQuestion(long questionId) {
        questionRepo.deleteQuestion(questionId);
        questionFilter.remove(questionId);
    }

    @Override
    public void updateQuestion(UpdateQuestionRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Question question = new Question(judgeManager, request.getId(), request.getTitle(), request.getContent(),
                request.getInputCase(), request.getOutputCase(), request.getQType(),
                request.getTag(), request.getDifficulty(), null, null,
                null, request.getTimeLimit(), request.getMemoryLimit(), null, null, now);

        questionRepo.updateQuestion(question);
    }

    @Override
    public QuestionVO getQuestionVO(long userId, long questionId) {

        // 获取文章基本信息
        QuestionVO questionVO = questionStruct.DOtoVO(getCacheQuestion(questionId));

        ThrowUtil.ifNull(questionVO, ErrorCode.NOT_FIND_ERROR, "Fail to view the question: the question does not exists.");

        // 获取用户对文章的点赞、收藏状态
        QuestionOps ops = questionRepo.getQuestionOps(userId, questionId);
        boolean isFavored = Objects.isNull(ops) ? false : ops.isFavored();
        int mark = Objects.isNull(ops) ? 0 : ops.getMark();

        questionVO.setFavored(isFavored);
        questionVO.setMark(mark);

        return questionVO;
    }


    /**
     * @param questionId 问题id
     * @return 问题基本信息（不包括用户的点赞、收藏状态）
     */
    public Question getCacheQuestion(long questionId) {
        // 通过布隆过滤器判断问题是否存在，不存在则直接返回
        if (!questionFilter.contains(questionId)) return null;

        String cacheKey = CacheKey.cacheQuestion(questionId);

        // 从缓存中获取文章
        return cacheRepo.cache(cacheKey, () -> questionRepo.getQuestionById(questionId),
                () -> onDeleteQuestionCache(questionId));
    }

    private void onDeleteQuestionCache(long questionId) {
        //持久化文章相关数据到数据库中
        long likeNum = cacheRepo.getLongValue(CacheKey.questionLikeCount(questionId));
        long favorNum = cacheRepo.getLongValue(CacheKey.questionFavorCount(questionId));
        long submitNum = cacheRepo.getLongValue(CacheKey.questionSubmitCount(questionId));
        long acceptNum = cacheRepo.getLongValue(CacheKey.questionPassCount(questionId));

        Question updatedQuestion = new Question();
        updatedQuestion.setLikeNum((int) likeNum);
        updatedQuestion.setFavorNum((int) favorNum);
        updatedQuestion.setSubmitNum((int) submitNum);
        updatedQuestion.setPassNum((int) acceptNum);

        questionRepo.updateQuestion(updatedQuestion);
    }

    @Override
    public void markQuestion(long userId, long questionId, int mark) {
        questionRepo.markQuestion(userId, questionId, mark);
    }

    @Override
    public void favorQuestion(long userId, long questionId, boolean isFavor) {
        questionRepo.favorQuestion(userId, questionId, isFavor);
    }

}
