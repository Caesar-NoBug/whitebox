package org.caesar.executor.service;

import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;

public interface ExecutorService {
    Response<ExecuteCodeResponse> executeCode(ExecuteCodeRequest request);
}
