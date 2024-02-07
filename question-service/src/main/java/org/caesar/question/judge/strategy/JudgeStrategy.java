package org.caesar.question.judge.strategy;

import org.caesar.common.vo.StatusMap;
import org.caesar.question.judge.StrategyType;

import java.util.List;

public interface JudgeStrategy {

    /**
     * 判断题目结果是否符合预期
     * @param codeResult 用户代码运行结果
     * @param outputCase 标准输出用例
     * @return 判题结果
     */
    StatusMap judge(List<String> codeResult, List<String> outputCase);

    //判断题目输出用例是否符合规定格式
    boolean testOutputCase(List<String> outputCase);

    //生成标准答案
    String generateAnswer(String outputCase);

    StrategyType getType();
}
