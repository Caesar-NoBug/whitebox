package org.caesar.client;

import org.caesar.common.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CompletableFuture;

@Async
@FeignClient(value = "user-service")
public interface UserClient {

    @GetMapping("/user/authorize")
    @ResponseBody
    CompletableFuture<Response<String>> authorize(@RequestParam String jwt, @RequestParam String requestPath);
    //Response<String> authorize(@RequestParam String jwt, @RequestParam String requestPath);
}
