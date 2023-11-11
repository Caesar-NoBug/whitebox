package org.caesar.util.strategy;

import org.caesar.common.vo.StatusMap;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

//适用于输出结果是一组值（各不相同），且结果为标准答案的任意次序皆可
@Component
public class UnorderedStrategy implements JudgeStrategy {

    private UnorderedStrategy(){}

    private static class InnerHolder {
        private static final UnorderedStrategy SINGLETON = new UnorderedStrategy();
    }

    public static UnorderedStrategy getInstance() {
        return InnerHolder.SINGLETON;
    }

    @Override
    public StatusMap judge(List<String> codeResult, List<String> outputCase) {

        int size = codeResult.size();
        StatusMap map = new StatusMap(size);
        HashSet<String> set = new HashSet<>();

        for (int i = 0; i < size; i++) {

            String[] result = codeResult.get(i).split(" ");
            String[] answer = outputCase.get(i).split(" ");

            for (int j = 0; j < result.length; j++) {
                //结果元素不唯一
                if (!set.add(result[j])){
                    map.setFail(i);
                    break;
                }
            }

            if(map.isFail(i)) continue;

            for (int j = 0; j < answer.length; j++) {
                //结果没有包含答案的元素
                if (!set.contains(answer[j])){
                    map.setFail(i);
                    break;
                }
            }

            set.clear();
        }

        return map;
    }

    @Override
    public boolean testOutputCase(List<String> outputCase) {

        HashSet<String> set = new HashSet<>();

        for (int i = 0; i < outputCase.size(); i++) {

            String[] answer = outputCase.get(i).split(" ");

            for (int j = 0; j < answer.length; j++) {
                //结果元素不唯一
                if (!set.add(answer[j]))
                    return false;
            }

        }

        return true;
    }

    @Override
    public String generateAnswer(String outputCase) {
        return outputCase;
    }

}
