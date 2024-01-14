package org.caesar.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.common.client.ExecutorClient;
import org.caesar.common.resp.RespUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.common.repository.CacheRepository;
import org.caesar.constant.CachePrefix;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.domain.question.response.JudgeCodeResponse;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.question.request.AddQuestionRequest;
import org.caesar.domain.question.request.JudgeCodeRequest;
import org.caesar.domain.question.request.UpdateQuestionRequest;
import org.caesar.model.po.QuestionPO;
import org.caesar.model.entity.Question;
import org.caesar.repository.QuestionRepository;
import org.caesar.service.QuestionService;
import org.caesar.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Service实现
 * @createDate 2023-08-30 10:04:07
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, QuestionPO>
        implements QuestionService {

    //TODO: 改成防腐层接口
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private ExecutorClient executorClient;

    //TODO: 优化校验逻辑，整合到Question中
    @Override
    public Response<Void> addQuestion(AddQuestionRequest request) {

        Long id = cacheRepository.nextId(CachePrefix.QUESTION_INC_ID);

        LocalDateTime now = LocalDateTime.now();

        Question question = new Question(id, request.getTitle(), request.getContent(),
                request.getInputCase(), request.getOutputCase(), request.getQType(),
                request.getTag(), request.getDifficulty(), 0, 0,
                0, request.getTimeLimit(), request.getMemoryLimit(), 0, now, now);

        boolean insertResult = questionRepository.addQuestion(question);

        ThrowUtil.ifTrue(!insertResult, ErrorCode.SYSTEM_ERROR, "添加问题失败");

        return Response.ok(null, "添加问题成功");
    }

    @Override
    public Response<Void> deleteQuestion(Long qId) {

        boolean deleteResult = questionRepository.deleteQuestion(qId);

        return deleteResult ? Response.ok(null, "删除成功")
                : Response.error("删除失败，题目不存在");
    }

    @Override
    public Response<Void> updateQuestion(UpdateQuestionRequest request) {
        LocalDateTime now = LocalDateTime.now();

        Question question = new Question(request.getId(), request.getTitle(), request.getContent(),
                request.getInputCase(), request.getOutputCase(), request.getQType(),
                request.getTag(), request.getDifficulty(), null, null,
                null, request.getTimeLimit(), request.getMemoryLimit(), null, null, now);

        boolean updateFlag = questionRepository.updateQuestion(question);

        return updateFlag ? Response.ok(null, "更新成功") : Response.error("更新失败");
    }

    @Override
    public Response<String> judgeCode(JudgeCodeRequest request) {

        //从数据库查询问题信息
        Question question = questionRepository.getQuestionById(request.getQId());

        ThrowUtil.ifNull(question, "指定问题不存在");

        String submitId = UUID.fastUUID().toString();

        doJudgeCode(submitId, request, question);

        return Response.ok(submitId);
    }

    @Async
    @Override
    //异步执行判题逻辑
    public void doJudgeCode(String submitId, JudgeCodeRequest request, Question question) {
        //向executor-service发起执行代码请求
        ExecuteCodeRequest executeRequest = new ExecuteCodeRequest(request.getCode(), request.getLanguage(),
                question.getInputArray(), question.getTimeLimit(), question.getMemoryLimit());

        ExecuteCodeResponse response = RespUtil.handleWithThrow(executorClient.executeCode(executeRequest), "fail to execute the code");

        //执行判题逻辑
        JudgeCodeResponse judgeCodeResponse = question.judge(response);

        //缓存判题结果
        cacheRepository.setObject(
                CachePrefix.SUBMIT_RESULT + request.getUserId() + ":" + submitId,
                judgeCodeResponse);

        //TODO: 定时批量同步到MySQL
    }

}




