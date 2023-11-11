package org.caesar.controller;

import org.caesar.common.Response;
import org.caesar.domain.request.executor.ExecuteCodeRequest;
import org.caesar.domain.response.executor.ExecuteCodeResponse;
import org.caesar.service.ExecutorService;
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
