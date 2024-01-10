package org.caesar.executor.service.impl;

import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.executor.sandbox.CodeSandbox;
import org.caesar.executor.sandbox.CodeSandboxFactory;
import org.caesar.executor.service.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExecutorServiceImpl implements ExecutorService {

    //允许的最大运行时间为5s
    public static final long MAX_TIME_LIMIT = 5000;
    //允许的最大运行内存为512MB
    public static final long MAX_MEMORY_LIMIT = 1 << 9;

    @Autowired
    private CodeSandboxFactory factory;

    @Override
    public Response<ExecuteCodeResponse> executeCode(ExecuteCodeRequest request) {

        CodeSandbox codeSandBox = factory.getCodeSandbox(request.getLanguage());
        //把所有Response.error改成抛异常再由ExceptionHandler处理的形式
        ThrowUtil.ifTrue(request.getTimeLimit() > MAX_TIME_LIMIT,
                 "代码运行时间超过允许的最大运行时间");

        ThrowUtil.ifTrue(request.getMemoryLimit() > MAX_MEMORY_LIMIT,
            "代码运行内存超过允许的最大运行内存");

        ThrowUtil.ifNull(codeSandBox, "不支持该语言的代码");

        return Response.ok(codeSandBox.executeCode(request));
    }

}
