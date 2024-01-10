package org.caesar.executor.controller;

import org.caesar.domain.common.vo.Response;
import org.caesar.domain.executor.request.ExecuteCodeRequest;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.executor.service.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecutorController {

    @Autowired
    private ExecutorService executorService;

    @PostMapping("/executeCode")
    public Response<ExecuteCodeResponse> executeCode(@RequestBody ExecuteCodeRequest request) {
        return executorService.executeCode(request);
    }

}
