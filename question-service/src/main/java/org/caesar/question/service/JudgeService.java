package org.caesar.question.service;

import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.domain.question.request.SubmitCodeRequest;
import org.caesar.domain.question.vo.SubmitCodeResultVO;
import org.caesar.domain.search.vo.PageVO;

public interface JudgeService {
    // 提交代码
    void submitCode(long userId, SubmitCodeRequest request);

    // 判断代码是否正确
    void judgeCode(long userId, ExecuteCodeResponse response);

    // 获取代码判断结果
    SubmitCodeResultVO getJudgeCodeResult(long userId, long qId, int submitId);

    // 问题助手: 给用户错误代码修正提示
    QuestionHelperResponse questionHelper(long userId, long questionId, int submitId);

    // 获取提交结果（不包含详细信息）
    PageVO<SubmitCodeResultVO> listSubmitResult(long userId, long questionId, int from, int size);
}
