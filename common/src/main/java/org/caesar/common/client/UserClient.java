package org.caesar.common.client;

import org.caesar.common.client.fallback.UserClientFallback;
import org.caesar.common.log.Logger;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.user.vo.RoleVO;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.domain.user.vo.UserPreferVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@FeignClient(value = "user-service", fallback = UserClientFallback.class)
public interface UserClient {

    @Logger(value = "[RPC] /getUserMin")
    @GetMapping("/user/min")
    Response<Map<Long, UserMinVO>> getUserMin(@RequestParam List<Long> id);

    @Logger(value = "[RPC] /getUserPrefer")
    @GetMapping("/user/prefer")
    Response<UserPreferVO> getUserPrefer();

    @Logger(value = "[RPC] /get-updated-role")
    @GetMapping("/user/updated-role")
    Response<List<RoleVO>> getUpdatedRole(@RequestParam LocalDateTime updateTime);

    @GetMapping("/user/circuit-breaker")
    Response<String> testCircuitBreaker();
}
