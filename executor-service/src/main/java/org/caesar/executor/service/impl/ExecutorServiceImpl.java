package org.caesar.executor.service.impl;

import org.caesar.common.exception.ExceptionHandler;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.executor.sandbox.CodeSandbox;
import org.caesar.executor.sandbox.CodeSandboxFactory;
import org.caesar.executor.service.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ExecutorServiceImpl implements ExecutorService {

    @Resource
    private CodeSandboxFactory factory;

    @Override
    public Response<ExecuteCodeResponse> executeCode(ExecuteCodeRequest request) {

        CodeSandbox codeSandBox = factory.getCodeSandbox(request.getLanguage());

        ThrowUtil.ifNull(codeSandBox, "Unsupported code language.");

        //TODO: 把所有Response.error改成抛异常再由ExceptionHandler处理的形式
        return Response.ok(codeSandBox.executeCode(request));
    }

}
