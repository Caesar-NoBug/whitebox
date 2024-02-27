package org.caesar.question.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.caesar.common.batch.cache.CacheIncTask;
import org.caesar.common.batch.cache.CacheIncTaskHandler;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.client.AIGCClient;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.log.Logger;
import org.caesar.common.resp.RespUtil;
import org.caesar.common.util.ListUtil;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.vo.SubmitCodeResultVO;
import org.caesar.domain.search.vo.PageVO;
import org.caesar.question.constant.CacheKey;
import org.caesar.question.judge.QuestionJudgeManager;
import org.caesar.question.model.MsResultStruct;
import org.caesar.question.model.entity.Question;
import org.caesar.question.model.entity.SubmitCodeResult;
import org.caesar.question.model.enums.JudgeStatus;
import org.caesar.question.publisher.ExecuteCodePublisher;
import org.caesar.question.repository.QuestionRepository;
import org.caesar.question.service.JudgeService;
import org.caesar.question.service.QuestionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private AIGCClient aigcClient;

    @Resource
    private ExecuteCodePublisher publisher;

    @Resource
    private QuestionRepository questionRepo;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private QuestionJudgeManager judgeManager;

    @Resource
    private CacheIncTaskHandler cacheIncTaskHandler;

    @Resource
    private MsResultStruct resultStruct;

    @Resource
    private QuestionService questionService;

    // 缓存中存储的判断结果的过期时间(半小时)
    public static final int CACHE_JUDGE_RESULT_EXPIRE = 30 * 60;

    @Override
    public QuestionHelperResponse questionHelper(long userId, long questionId, int submitId) {

        System.out.println("service thread:" + Thread.currentThread().getId());

        Question question = questionService.getCacheQuestion(questionId);
        SubmitCodeResultVO judgeCodeResult = getJudgeCodeResult(userId, questionId, submitId);

        QuestionHelperRequest questionHelperRequest = new QuestionHelperRequest();
        questionHelperRequest.setDescription(question.getContent());
        questionHelperRequest.setCode(judgeCodeResult.getCode());
        questionHelperRequest.setCorrectCode(question.getCorrectCode());
        questionHelperRequest.setMessage(judgeCodeResult.getMessage());
        questionHelperRequest.setResult(judgeCodeResult.getResult());

        return RespUtil.handleWithThrow(aigcClient.questionHelper(questionHelperRequest),
                "Fail to access AIGC service on question helper.");
    }

    @Override
    public PageVO<SubmitCodeResultVO> listSubmitResult(long userId, long questionId, int from, int size) {
        Page<SubmitCodeResult> page = questionRepo.listSubmitResult(userId, questionId, from, size);
        PageVO<SubmitCodeResultVO> pageVO = new PageVO<>();
        pageVO.setData(ListUtil.convert(page.getRecords(), resultStruct::DOtoVO));
        pageVO.setTotalSize((int) page.getTotal());
        return pageVO;
    }

    @Override
    public void submitCode(long userId, SubmitCodeRequest request) {

        long questionId = request.getQuestionId();
        int submitId = request.getSubmitId();

        // 获取问题信息
        Question question = questionService.getCacheQuestion(questionId);

        ThrowUtil.ifNull(question, "The question does not exists.");

        // 构造请求并发出执行代码的消息
        ExecuteCodeRequest executeRequest = new ExecuteCodeRequest(questionId, submitId, request.getCode(), request.getLanguage(),
                question.getInputArray(), question.getTimeLimit(), question.getMemoryLimit());

        publisher.sendExecuteCodeMessage(executeRequest);

        SubmitCodeResult judgingResult = new SubmitCodeResult();
        judgingResult.setUserId(userId);
        judgingResult.setQuestionId(questionId);
        judgingResult.setSubmitId(submitId);
        judgingResult.setCode(request.getCode());
        judgingResult.setLanguage(request.getLanguage());

        // 设置判题结果缓存为判题中
        questionRepo.addSubmitResult(judgingResult);

        String submitKey = CacheKey.questionSubmitCount(questionId);
        cacheIncTaskHandler.addTask(submitKey, new CacheIncTask(1));
    }

    @Logger(value = "judgeCode", args = true, result = true)
    @Override
    public void judgeCode(long userId, ExecuteCodeResponse response) {

        long qId = response.getQuestionId();

        int submitId = response.getSubmitId();

        Question question = questionService.getCacheQuestion(qId);

        // 获取判题结果缓存信息
        SubmitCodeResult submitCodeResult = questionRepo.getSubmitResult(userId, qId, submitId);

        //执行判题逻辑
        question.judge(judgeManager, response, submitCodeResult);

        questionRepo.updateSubmitResult(submitCodeResult);

        // 如果通过了，增加通过题目的计数
        if (submitCodeResult.getStatus() == JudgeStatus.ACCEPTED.getCode()) {
            String acceptKey = CacheKey.questionPassCount(qId);
            cacheIncTaskHandler.addTask(acceptKey, new CacheIncTask(1));
        }

    }

    @Override
    public SubmitCodeResultVO getJudgeCodeResult(long userId, long qId, int submitId) {

        SubmitCodeResultVO result = resultStruct.DOtoVO(questionRepo.getSubmitResult(userId, qId, submitId));

        ThrowUtil.ifNull(result, ErrorCode.NOT_FIND_ERROR, "The submit code request does not exist or judge failed.");

        ThrowUtil.ifTrue(result.getStatus() == JudgeStatus.JUDGING.getCode()
                , ErrorCode.REQUEST_PROCESSING_ERROR, "The code is been judging, please wait.");

        return result;
    }

}
