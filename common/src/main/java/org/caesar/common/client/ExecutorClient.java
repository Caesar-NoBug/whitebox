package org.caesar.common.client;

import org.caesar.common.client.fallback.ExecutorClientFallback;
import org.caesar.common.log.Logger;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "executor-service", fallback = ExecutorClientFallback.class)
public interface ExecutorClient {

    @Logger("[RPC] /executeCode")
    @PostMapping("/executeCode")
    Response<ExecuteCodeResponse> executeCode(@RequestBody ExecuteCodeRequest request);

}
