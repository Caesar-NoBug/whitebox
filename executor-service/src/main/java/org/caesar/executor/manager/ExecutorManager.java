package org.caesar.executor.manager;

import org.caesar.common.exception.ExceptionHandler;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.executor.publisher.ExecuteCodeRespPublisher;
import org.caesar.executor.service.ExecutorService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ExecutorManager {

    @Resource
    private ExecutorService executorService;

    @Resource
    private ExceptionHandler exceptionHandler;

    @Resource
    private ExecuteCodeRespPublisher executeCodeRespPublisher;

    public void executeCode(ExecuteCodeRequest request) {

        Response<ExecuteCodeResponse> response = exceptionHandler.handleException(
                () -> executorService.executeCode(request));

        executeCodeRespPublisher.sendExecuteCodeRespMessage(response);
    }
}
