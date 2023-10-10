package org.caesar.client;

import org.caesar.common.Response;
import org.caesar.common.model.dto.request.executor.ExecuteCodeRequest;
import org.caesar.common.model.dto.response.executor.ExecuteCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("executor-service")
public interface ExecutorClient {

    @PostMapping("/executeCode")
    Response<ExecuteCodeResponse> executorCode(@RequestBody ExecuteCodeRequest request);

}
