package org.caesar.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.common.check.CheckManager;
import org.caesar.client.ExecutorClient;
import org.caesar.common.Response;
import org.caesar.constant.RedisPrefix;
import org.caesar.common.constant.enums.CodeResultType;
import org.caesar.common.constant.enums.ErrorCode;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.model.dto.request.executor.ExecuteCodeRequest;
import org.caesar.common.model.dto.response.executor.ExecuteCodeResponse;
import org.caesar.common.model.dto.response.question.SubmitCodeResponse;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.model.dto.request.question.AddQuestionRequest;
import org.caesar.common.model.dto.request.question.SubmitCodeRequest;
import org.caesar.common.model.dto.request.question.UpdateQuestionRequest;
import org.caesar.model.dao.QuestionDO;
import org.caesar.model.vo.JudgeParam;
import org.caesar.service.QuestionService;
import org.caesar.mapper.QuestionMapper;
import org.caesar.util.QuestionJudgeManager;
import org.caesar.common.util.RedisCache;
import org.caesar.common.util.StatusMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Service实现
 * @createDate 2023-08-30 10:04:07
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, QuestionDO>
        implements QuestionService {

    public static final String WRONG_ANSWER_MESSAGE = "运行结果错误\n\t运行结果:\n%s\n\t目标结果:\n%s";

    //TODO: 改成防腐层接口
    @Autowired
    private QuestionMapper mapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ExecutorClient executorClient;

    //TODO: 优化校验逻辑，整合到Question中
    @Override
    public Response<Void> addQuestion(AddQuestionRequest request) {
        String inputCase = request.getInputCase();
        String rawOutputCase = request.getOutputCase();
        Integer qType = request.getQType();

        List<String> outputCase = null;

        try {
            JSON.parseArray(inputCase, String.class);
            outputCase = JSON.parseArray(rawOutputCase, String.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ILLEGAL_PARAM_ERROR, "非法输入用例或输出用例：无法解析为数组");
        }

        ThrowUtil.throwIf(!QuestionJudgeManager.checkOutputCase(qType, outputCase),
                "非法输出用例：不符合题目类型要求");

        //redis也改成防腐层接口
        Long id = redisCache.nextId(RedisPrefix.QUESTION_INC_ID);

        LocalDateTime now = LocalDateTime.now();

        QuestionDO questionDO = new QuestionDO(id, request.getTitle(), request.getContent(),
                inputCase, rawOutputCase, qType,
                request.getTag(), 0, 0, 0,
                0, request.getTimeLimit(), request.getMemoryLimit(), 0, now, now);

        int insertResult = mapper.insert(questionDO);

        ThrowUtil.throwIf(insertResult <= 0, ErrorCode.SYSTEM_ERROR, "添加问题失败");

        return Response.ok(null, "添加问题成功");
    }

    @Override
    public Response<Void> deleteQuestion(Long qId) {

        int deleteResult = mapper.deleteById(qId);

        if (deleteResult <= 0)
            return Response.error("删除失败，题目不存在");

        return Response.ok(null, "删除成功");
    }

    @Override
    public Response<Void> updateQuestion(UpdateQuestionRequest request) {
        String inputCase = request.getInputCase();
        String rawOutputCase = request.getOutputCase();
        Integer qType = request.getQType();

        List<String> outputCase = null;
        try {
            JSON.parseArray(inputCase, String.class);
            outputCase = JSON.parseArray(rawOutputCase, String.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ILLEGAL_PARAM_ERROR, "非法输入用例或输出用例：无法解析为数组");
        }

        ThrowUtil.throwIf(!QuestionJudgeManager.checkOutputCase(qType, outputCase),
                "非法输出用例：不符合题目类型要求");

        QuestionDO questionDO = new QuestionDO(request.getId(), request.getTitle(),
                request.getContent(), inputCase, rawOutputCase, qType,
                request.getTag(), request.getDifficulty(), request.getThumbNum(),
                request.getFavorNum(), request.getSubmitNum(),
                request.getTimeLimit(), request.getMemoryLimit(), 0, null, LocalDateTime.now());

        boolean updateFlag = mapper.updateById(questionDO) > 0;

        return updateFlag ? Response.ok(null, "更新成功") : Response.error("更新失败");
    }

    @Override
    public SubmitCodeResponse submitCode(SubmitCodeRequest request) {

        CheckManager.checkThrowException(request, SubmitCodeRequest.class);

        //从数据库查询问题信息
        QuestionDO questionDO = mapper.selectById(request.getQId());

        ThrowUtil.throwIfNull(Objects.isNull(questionDO), "指定问题不存在");

        List<String> inputCase = JSON.parseArray(questionDO.getInputCase(), String.class);
        List<String> outputCase = JSON.parseArray(questionDO.getOutputCase(), String.class);
        Integer qType = questionDO.getQType();

        //向executor-service发起执行代码请求
        ExecuteCodeRequest executeRequest = new ExecuteCodeRequest(request.getCode(), request.getLanguage(),
                inputCase, questionDO.getTimeLimit(), questionDO.getMemoryLimit());

        //TODO: 写成异步请求并将结果保存到redis中
        Response<ExecuteCodeResponse> response = executorClient.executorCode(executeRequest);

        //处理响应结果
        ThrowUtil.throwIf(response.getCode() != ErrorCode.SUCCESS.getCode(),
                response.getCode(), response.getMsg());

        ExecuteCodeResponse executeResponse = response.getData();

        String message = executeResponse.getMessage();
        List<CodeResultType> resultTypes = executeResponse.getType();
        List<Long> time = executeResponse.getTime();
        List<Long> memory = executeResponse.getMemory();

        //执行出现异常则直接返回
        if (!executeResponse.isSuccess()) {
            return new SubmitCodeResponse(false, executeResponse.getType(), message, time, memory);
        }

        List<String> codeResult = executeResponse.getResult();

        StatusMap map = QuestionJudgeManager.judge(new JudgeParam(codeResult, outputCase, qType));

        boolean success = true;

        SubmitCodeResponse submitResponse = new SubmitCodeResponse();

        for (int i = 0; i < codeResult.size(); i++) {

            //无异常则判断结果是否正确
            if (CodeResultType.TEMPORARY_ACCEPTED.equals(resultTypes.get(i))) {
                if (map.isFail(i)) {
                    resultTypes.set(i, CodeResultType.WRONG_ANSWER);

                    //返回第一个不匹配的错误信息
                    if (success) {
                        String wrongResult = codeResult.get(i);
                        String correctResult = QuestionJudgeManager.generateAnswer(qType, outputCase.get(i));
                        submitResponse.setMessage(String.format(WRONG_ANSWER_MESSAGE, wrongResult, correctResult));
                        success = false;
                    }

                } else
                    resultTypes.set(i, CodeResultType.ACCEPTED);
            }
            //有异常则直接返回
            else if (success) {
                submitResponse.setMessage(executeResponse.getMessage());
                success = false;
            }

        }

        return submitResponse;
    }

}




