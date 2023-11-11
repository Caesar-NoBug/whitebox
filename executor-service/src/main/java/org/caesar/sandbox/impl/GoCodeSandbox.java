package org.caesar.sandbox.impl;

import org.caesar.common.TaskResult;
import org.caesar.domain.constant.enums.CodeLanguage;
import org.caesar.domain.response.executor.ExecuteCodeResponse;
import org.caesar.sandbox.CodeSandbox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoCodeSandbox extends CodeSandbox {

    private GoCodeSandbox(){}

    private static class InnerHolder {
        private static final GoCodeSandbox SINGLE_TON = new GoCodeSandbox();
    }

    public static GoCodeSandbox getInstance() {
        return InnerHolder.SINGLE_TON;
    }

    @Override
    public CodeLanguage getLanguage() {
        return CodeLanguage.GO;
    }

    @Override
    protected TaskResult<String> saveCode(String code) {
        return null;
    }

    @Override
    protected TaskResult<Void> compileCode(String userCodeDir) {
        return null;
    }

    @Override
    protected TaskResult<ExecuteCodeResponse> runCode(String compiledFile, List<String> inputCase, long timeLimit, long memoryLimit) {
        return null;
    }
}
