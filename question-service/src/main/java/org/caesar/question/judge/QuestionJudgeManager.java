package org.caesar.question.judge;

import org.caesar.common.vo.StatusMap;
import org.caesar.question.judge.strategy.DefaultStrategy;
import org.caesar.question.judge.strategy.JudgeStrategy;
import org.caesar.question.judge.strategy.RoundStrategy;
import org.caesar.question.judge.strategy.UnorderedStrategy;
import org.caesar.question.model.vo.JudgeParam;

import java.util.List;

public class QuestionJudgeManager {

    public static StatusMap judge(JudgeParam judgeParam) {
        JudgeStrategy strategy = getJudgeStrategy(judgeParam.getType());
        return strategy.judge(judgeParam.getCodeResult(), judgeParam.getOutputCase());
    }

    public static Boolean checkOutputCase(int type, List<String> outputCase) {
        JudgeStrategy strategy = getJudgeStrategy(type);
        return strategy.testOutputCase(outputCase);
    }

    public static String generateAnswer(int type, String outputCase) {
        JudgeStrategy strategy = getJudgeStrategy(type);
        return strategy.generateAnswer(outputCase);
    }

    private static JudgeStrategy getJudgeStrategy(int type) {

        switch (type) {
            case 0 : return DefaultStrategy.getInstance();
            case 1 : return RoundStrategy.getInstance();
            case 2 : return UnorderedStrategy.getInstance();
            default: throw new IllegalArgumentException("非法问题类型：" + type);
        }

    }
}
