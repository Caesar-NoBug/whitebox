package org.caesar.controller;

import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<Response<?>> userServiceFallback() {
        return Mono.just(Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[User Service] service unavailable, please wait."));
    }

    @GetMapping("/article-service")
    public Mono<Response<?>> articleServiceFallback() {
        return Mono.just(Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Article Service] service unavailable, please wait."));
    }

    @GetMapping("/aigc-service")
    public Mono<Response<?>> aigcServiceFallback() {
        return Mono.just(Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[AIGC Service] service unavailable, please wait."));
    }

    @GetMapping("/question-service")
    public Mono<Response<?>> questionServiceFallback() {
        return Mono.just(Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Question Service] service unavailable, please wait."));
    }

    @GetMapping("/search-service")
    public Mono<Response<?>> searchServiceFallback() {
        return Mono.just(Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Search Service] service unavailable, please wait."));
    }

    @GetMapping("/executor-service")
    public Mono<Response<?>> executorServiceFallback() {
        return Mono.just(Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Executor Service] service unavailable, please wait."));
    }

}
