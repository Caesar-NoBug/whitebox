package org.caesar.service;

import org.caesar.common.Response;
import org.caesar.domain.request.executor.ExecuteCodeRequest;
import org.caesar.domain.response.executor.ExecuteCodeResponse;

public interface ExecutorService {
    Response<ExecuteCodeResponse> executeCode(ExecuteCodeRequest request);
}
