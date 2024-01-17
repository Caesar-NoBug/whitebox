package org.caesar.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//处理基本的限流操作
//TODO： 熔断处理器, 用nacos加sentinel进行限流
//TODO: 限制验证码访问频率
@Component
public class RateLimiterFilter implements GlobalFilter, Ordered {

    //TODO: 加一个ip黑名单，用户id黑名单
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -9;
    }
}
