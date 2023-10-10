package org.caesar.filter;

import com.alibaba.fastjson.JSON;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.caesar.common.model.dto.request.question.SubmitCodeRequest;
import org.caesar.publisher.ExecuteMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

//处理基本的限流操作
//TODO： 熔断处理器, 用nacos加sentinel进行限流
@Component
public class RateLimiterGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -9;
    }

}
