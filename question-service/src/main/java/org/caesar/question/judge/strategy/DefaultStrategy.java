package org.caesar.question.judge.strategy;

import org.caesar.common.vo.StatusMap;
import org.caesar.question.judge.StrategyType;
import org.springframework.stereotype.Component;

import java.util.BitSet;
import java.util.List;
import java.util.Objects;

//默认判题策略：精准匹配
@Component
public class DefaultStrategy implements JudgeStrategy {

    @Override
    public StatusMap judge(List<String> codeResult, List<String> outputCase) {

        BitSet bitSet = new BitSet();
        bitSet.nextClearBit(23);

        int size = codeResult.size();
        StatusMap map = new StatusMap(size);

        for (int i = 0; i < size; i++) {

            String result = codeResult.get(i);
            if (Objects.isNull(result) || !Objects.equals(result, outputCase.get(i)))
                map.setFail(i);
        }

        return map;
    }

    @Override
    public boolean testOutputCase(List<String> outputCase) {
        return true;
    }

    @Override
    public String generateAnswer(String outputCase) {
        return outputCase;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.DEFAULT;
    }
}
