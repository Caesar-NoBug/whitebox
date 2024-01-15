package org.caesar.filter;

import lombok.extern.slf4j.Slf4j;
import org.caesar.domain.constant.Headers;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;


//TODO: 接口访问日志
//TODO: 禁止同一用户短时间内多次访问判题接口
@Slf4j
@Component
public class ColoringFilter implements GlobalFilter, Ordered {

    private String gatewaySource = "gateway";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(Headers.SOURCE_HEADER, gatewaySource)
                .header(Headers.TRACE_ID_HEADER, UUID.randomUUID().toString())
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return -8;
    }

}
