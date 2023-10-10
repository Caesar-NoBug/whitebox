package org.caesar.util;

import org.caesar.common.util.StatusMap;
import org.caesar.model.vo.JudgeParam;
import org.caesar.util.strategy.DefaultStrategy;
import org.caesar.util.strategy.JudgeStrategy;
import org.caesar.util.strategy.RoundStrategy;
import org.caesar.util.strategy.UnorderedStrategy;

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
            case 0 -> {
                return DefaultStrategy.getInstance();
            }
            case 1 -> {
                return RoundStrategy.getInstance();
            }
            case 2 -> {
                return UnorderedStrategy.getInstance();
            }
            default -> throw new IllegalArgumentException("非法问题类型：" + type);
        }
    }
}
