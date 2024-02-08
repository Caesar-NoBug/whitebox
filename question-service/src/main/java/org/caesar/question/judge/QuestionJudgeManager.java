package org.caesar.question.judge;

import org.caesar.common.vo.StatusMap;
import org.caesar.question.judge.strategy.JudgeStrategy;
import org.caesar.question.model.vo.JudgeParam;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QuestionJudgeManager implements ApplicationContextAware {

    private final Map<Integer, JudgeStrategy> judgeStrategyMap = new ConcurrentHashMap<>();

    public StatusMap judge(JudgeParam judgeParam) {
        JudgeStrategy strategy = getJudgeStrategy(judgeParam.getType());
        return strategy.judge(judgeParam.getCodeResult(), judgeParam.getOutputCase());
    }

    public Boolean checkOutputCase(int type, List<String> outputCase) {
        JudgeStrategy strategy = getJudgeStrategy(type);
        return strategy.testOutputCase(outputCase);
    }

    public String generateAnswer(int type, String outputCase) {
        JudgeStrategy strategy = getJudgeStrategy(type);
        return strategy.generateAnswer(outputCase);
    }

    private JudgeStrategy getJudgeStrategy(int type) {
        return judgeStrategyMap.get(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, JudgeStrategy> tempMap = applicationContext.getBeansOfType(JudgeStrategy.class);
        tempMap.values().forEach(strategy -> judgeStrategyMap.put(strategy.getType().getValue(), strategy));
    }
}
