package org.caesar.sandbox.impl;

import org.caesar.common.TaskResult;
import org.caesar.common.constant.enums.CodeLanguage;
import org.caesar.common.model.dto.response.executor.ExecuteCodeResponse;
import org.caesar.sandbox.CodeSandbox;

import java.util.List;


public class CLangCodeSandbox extends CodeSandbox {

    private CLangCodeSandbox(){}

    private static class InnerHolder {
        private static final CLangCodeSandbox SINGLE_TON = new CLangCodeSandbox();
    }

    public static CLangCodeSandbox getInstance() {
        return InnerHolder.SINGLE_TON;
    }

    @Override
    public CodeLanguage getLanguage() {
        return CodeLanguage.C;
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
