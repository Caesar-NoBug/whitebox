package org.caesar.sandbox.impl;

import org.caesar.common.vo.TaskResult;
import org.caesar.domain.executor.enums.CodeLanguage;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.sandbox.CodeSandbox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CPlusPlusCodeSandbox extends CodeSandbox {

    private CPlusPlusCodeSandbox(){}

    private static class InnerHolder {
        private static final CPlusPlusCodeSandbox SINGLE_TON = new CPlusPlusCodeSandbox();
    }

    public static CPlusPlusCodeSandbox getInstance() {
        return InnerHolder.SINGLE_TON;
    }

    @Override
    public CodeLanguage getLanguage() {
        return CodeLanguage.CPP;
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
