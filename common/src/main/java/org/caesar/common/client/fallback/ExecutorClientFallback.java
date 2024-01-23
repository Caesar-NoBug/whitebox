package org.caesar.common.client.fallback;

import org.caesar.common.client.ExecutorClient;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

@Component
public class ExecutorClientFallback implements ExecutorClient {

    @Override
    public Response<ExecuteCodeResponse> executeCode(ExecuteCodeRequest request) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Executor Service] 'executeCode' service unavailable");
    }

}
