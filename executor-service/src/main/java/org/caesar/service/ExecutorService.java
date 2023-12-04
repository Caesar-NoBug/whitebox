package org.caesar.service;

import org.caesar.common.vo.Response;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;

public interface ExecutorService {
    Response<ExecuteCodeResponse> executeCode(ExecuteCodeRequest request);
}
