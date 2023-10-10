package org.caesar.service;

import org.caesar.common.Response;
import org.caesar.common.model.dto.request.executor.ExecuteCodeRequest;
import org.caesar.common.model.dto.response.executor.ExecuteCodeResponse;

public interface ExecutorService {
    Response<ExecuteCodeResponse> executeCode(ExecuteCodeRequest request);
}
