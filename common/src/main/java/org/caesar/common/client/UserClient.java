package org.caesar.common.client;

import org.caesar.common.vo.Response;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.domain.user.vo.UserPreferVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@FeignClient(value = "user-service")
public interface UserClient {

    //@Async
    @GetMapping("/auth/authorize")
    @ResponseBody
    //TODO: 改一下这个方法的写法和反序列化的过程
    CompletableFuture<Response<Long>> authorize(@RequestParam String jwt, @RequestParam String requestPath);

    @GetMapping("/user/min")
    @ResponseBody
    Response<Map<Long, UserMinVO>> getUserMin(@RequestParam List<Long> id);

    @GetMapping("/user/prefer")
    @ResponseBody
    Response<UserPreferVO> getUserPrefer();

}
