package org.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.common.repository.CacheRepository;
import org.caesar.constant.CacheKey;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.domain.question.response.JudgeCodeResponse;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.model.po.QuestionPO;
import org.caesar.model.entity.Question;
import org.caesar.publisher.ExecuteCodePublisher;
import org.caesar.repository.QuestionRepository;
import org.caesar.service.QuestionService;
import org.caesar.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Service实现
 * @createDate 2023-08-30 10:04:07
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, QuestionPO>
        implements QuestionService {

    //TODO: 改成防腐层接口
    @Resource
    private QuestionRepository questionRepo;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private ExecuteCodePublisher publisher;

    // 缓存中存储的判断结果的过期时间(半小时)
    public static final int CACHE_JUDGE_RESULT_EXPIRE = 30 * 60;

    //2023-01-01 00:00
    long BEGIN_TIMESTAMP = 1672531200000L;

    @Override
    public void addQuestion(AddQuestionRequest request) {

        Long id = cacheRepo.nextId(CacheKey.QUESTION_INC_ID);

        LocalDateTime now = LocalDateTime.now();

        Question question = new Question(id, request.getTitle(), request.getContent(),
                request.getInputCase(), request.getOutputCase(), request.getQType(),
                request.getTag(), request.getDifficulty(), 0, 0,
                0, request.getTimeLimit(), request.getMemoryLimit(), 0, now, now);

        boolean insertResult = questionRepo.addQuestion(question);

        ThrowUtil.ifTrue(!insertResult, ErrorCode.SYSTEM_ERROR, "Fail to add the question.");

    }

    @Override
    public void deleteQuestion(Long qId) {
        ThrowUtil.ifFalse(questionRepo.deleteQuestion(qId), ErrorCode.NOT_FIND_ERROR,
                "Question does not exist or fail to remove the question.");
    }

    @Override
    public void updateQuestion(UpdateQuestionRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Question question = new Question(request.getId(), request.getTitle(), request.getContent(),
                request.getInputCase(), request.getOutputCase(), request.getQType(),
                request.getTag(), request.getDifficulty(), null, null,
                null, request.getTimeLimit(), request.getMemoryLimit(), null, null, now);

        boolean updateFlag = questionRepo.updateQuestion(question);

        ThrowUtil.ifFalse(updateFlag, ErrorCode.NOT_FIND_ERROR, "Question does not exist or fail to update the question.");
    }

    @Override
    public void submitCode(long userId, SubmitCodeRequest request) {

        long qId = request.getQId();
        int submitId = (int) ((System.currentTimeMillis() - BEGIN_TIMESTAMP) / 1000);

        // 获取问题信息
        Question question = getCacheQuestion(qId);

        ThrowUtil.ifNull(question, "The question does not exists.");

        // 构造请求并发出执行代码的消息
        ExecuteCodeRequest executeRequest = new ExecuteCodeRequest(qId, submitId, request.getCode(), request.getLanguage(),
                question.getInputArray(), question.getTimeLimit(), question.getMemoryLimit());

        publisher.sendExecuteCodeMessage(executeRequest);

        String submitResultKey = CacheKey.getSubmitResultKey(userId, qId, submitId);

        cacheRepo.setObject(submitResultKey, JudgeCodeResponse.judging(), CACHE_JUDGE_RESULT_EXPIRE, TimeUnit.SECONDS);
    }

    // 从缓存中获取问题信息
    private Question getCacheQuestion(long id) {
        return cacheRepo.handleCache(CacheKey.CACHE_QUESTION + id, () -> questionRepo.getQuestionById(id));
    }

    @Override
    //异步执行判题逻辑
    public void judgeCode(long userId, ExecuteCodeResponse response) {

        long qId = response.getQuestionId();

        Question question = getCacheQuestion(qId);

        //执行判题逻辑
        JudgeCodeResponse judgeCodeResponse = question.judge(response);

        String submitResultKey = CacheKey.getSubmitResultKey(userId, qId, response.getSubmitId());

        //缓存判题结果
        cacheRepo.setObject(submitResultKey, judgeCodeResponse);

        //TODO: 定时批量同步到MySQL
    }

    @Override
    public JudgeCodeResponse getJudgeCodeResult(long userId, long qId, int submitId) {

        String submitResultKey = CacheKey.getSubmitResultKey(userId, qId, submitId);

        JudgeCodeResponse result = cacheRepo.handleCache(submitResultKey, () -> questionRepo.getJudgeResult(qId, submitId));

        ThrowUtil.ifNull(result, ErrorCode.NOT_FIND_ERROR, "The submit code request does not exist or judge failed.");

        ThrowUtil.ifFalse(result.isComplete(), ErrorCode.REQUEST_PROCESSING_ERROR, "The code is been judging, please wait.");

        return result;
    }

}
