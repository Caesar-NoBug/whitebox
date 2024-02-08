package org.caesar.question.service.impl;

import org.caesar.common.batch.cache.CacheIncTask;
import org.caesar.common.batch.cache.CacheIncTaskHandler;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.client.AIGCClient;
import org.caesar.common.log.Logger;
import org.caesar.common.resp.RespUtil;
import org.caesar.common.util.DataFilter;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.question.vo.QuestionVO;
import org.caesar.question.constant.CacheKey;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.domain.question.response.SubmitCodeResult;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.question.judge.QuestionJudgeManager;
import org.caesar.question.model.MsQuestionStruct;
import org.caesar.question.model.entity.Question;
import org.caesar.question.model.entity.QuestionOps;
import org.caesar.question.publisher.ExecuteCodePublisher;
import org.caesar.question.repository.QuestionRepository;
import org.caesar.question.service.QuestionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionRepository questionRepo;

    @Resource
    private MsQuestionStruct questionStruct;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private AIGCClient aigcClient;

    @Resource
    private ExecuteCodePublisher publisher;

    @Resource
    private QuestionJudgeManager judgeManager;

    @Resource
    private CacheIncTaskHandler cacheIncTaskHandler;

    @Resource
    private DataFilter questionFilter;

    // 缓存中存储的判断结果的过期时间(半小时)
    public static final int CACHE_JUDGE_RESULT_EXPIRE = 30 * 60;

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

        ThrowUtil.ifNull(questionVO, ErrorCode.NOT_FIND_ERROR, "Fail to check the question: the question does not exists.");

        // 获取用户对文章的点赞、收藏状态
        QuestionOps ops = questionRepo.getQuestionOps(userId, questionId);
        questionVO.setFavored(ops.isFavored());
        questionVO.setMark(ops.getMark());

        return questionVO;
    }


    /**
     * @param questionId 问题id
     * @return 问题基本信息（不包括用户的点赞、收藏状态）
     */
    Question getCacheQuestion(long questionId) {
        // 通过布隆过滤器判断问题是否存在，不存在则直接返回
        if (!questionFilter.contains(questionId)) return null;

        String cacheKey = CacheKey.cacheQuestion(questionId);

        // 从缓存中获取文章
        return cacheRepo.cache(cacheKey, () -> questionRepo.getQuestionById(questionId),
                () -> onDeleteQuestionCache(questionId));
    }

    @Override
    public QuestionHelperResponse questionHelper(long userId, long questionId, int submitId) {

        System.out.println("service thread:" + Thread.currentThread().getId());

        Question question = getCacheQuestion(questionId);
        SubmitCodeResult judgeCodeResult = getJudgeCodeResult(userId, questionId, submitId);

        QuestionHelperRequest questionHelperRequest = new QuestionHelperRequest();
        questionHelperRequest.setDescription(question.getContent());
        questionHelperRequest.setCode(judgeCodeResult.getCode());
        questionHelperRequest.setCorrectCode(question.getCorrectCode());
        questionHelperRequest.setMessage(judgeCodeResult.getMessage());
        questionHelperRequest.setResult(judgeCodeResult.getResult());

        return RespUtil.handleWithThrow(aigcClient.questionHelper(questionHelperRequest),
                "Fail to access AIGC service with question helper.");
    }

    private void onDeleteQuestionCache(long questionId) {
        //持久化文章相关数据到数据库中
        long likeNum = cacheRepo.getLongValue(CacheKey.questionLikeCount(questionId));
        long favorNum = cacheRepo.getLongValue(CacheKey.questionFavorCount(questionId));
        long submitNum = cacheRepo.getLongValue(CacheKey.questionSubmitCount(questionId));
        long acceptNum = cacheRepo.getLongValue(CacheKey.questionAcceptCount(questionId));

        Question updatedQuestion = new Question();
        updatedQuestion.setLikeNum((int) likeNum);
        updatedQuestion.setFavorNum((int) favorNum);
        updatedQuestion.setSubmitNum((int) submitNum);
        updatedQuestion.setAcceptNum((int) acceptNum);

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

    @Override
    public void submitCode(long userId, SubmitCodeRequest request) {

        long questionId = request.getQuestionId();
        int submitId = request.getSubmitId();

        // 获取问题信息
        Question question = getCacheQuestion(questionId);

        ThrowUtil.ifNull(question, "The question does not exists.");

        // 构造请求并发出执行代码的消息
        ExecuteCodeRequest executeRequest = new ExecuteCodeRequest(questionId, submitId, request.getCode(), request.getLanguage(),
                question.getInputArray(), question.getTimeLimit(), question.getMemoryLimit());

        publisher.sendExecuteCodeMessage(executeRequest);

        String submitResultKey = CacheKey.getSubmitResultKey(userId, questionId, submitId);

        SubmitCodeResult judgingResult = new SubmitCodeResult();
        judgingResult.setCode(request.getCode());

        // 设置判题结果缓存为判题中
        cacheRepo.setObject(submitResultKey, judgingResult, CACHE_JUDGE_RESULT_EXPIRE, TimeUnit.SECONDS);

        String submitKey = CacheKey.questionSubmitCount(questionId);
        cacheIncTaskHandler.addTask(submitKey, new CacheIncTask(1));
    }

    @Logger(value = "judgeCode", args = true, result = true)
    @Override
    public void judgeCode(long userId, ExecuteCodeResponse response) {

        long qId = response.getQuestionId();

        int submitId = response.getSubmitId();

        Question question = getCacheQuestion(qId);

        //执行判题逻辑
        SubmitCodeResult submitCodeResult = question.judge(judgeManager, response);

        String submitResultKey = CacheKey.getSubmitResultKey(userId, qId, submitId);

        SubmitCodeResult judgingResult = cacheRepo.getObject(submitResultKey);

        // 保存用户代码到判题结果中
        submitCodeResult.setCode(judgingResult.getCode());

        //缓存判题结果
        cacheRepo.setObject(submitResultKey, submitCodeResult, 10, TimeUnit.MINUTES);

        questionRepo.addSubmitResult(userId, qId, submitId, submitCodeResult);

        if (submitCodeResult.isPassed()) {
            String acceptKey = CacheKey.questionAcceptCount(qId);
            cacheIncTaskHandler.addTask(acceptKey, new CacheIncTask(1));
        }

    }

    @Override
    public SubmitCodeResult getJudgeCodeResult(long userId, long qId, int submitId) {

        String submitResultKey = CacheKey.getSubmitResultKey(userId, qId, submitId);

        SubmitCodeResult result = cacheRepo.cache(submitResultKey, () -> questionRepo.getSubmitResult(userId, qId, submitId));

        ThrowUtil.ifNull(result, ErrorCode.NOT_FIND_ERROR, "The submit code request does not exist or judge failed.");

        ThrowUtil.ifFalse(result.isComplete(), ErrorCode.REQUEST_PROCESSING_ERROR, "The code is been judging, please wait.");

        return result;
    }

}
