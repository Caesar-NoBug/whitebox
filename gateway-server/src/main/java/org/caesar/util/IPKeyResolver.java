package org.caesar.util;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;


public class IPKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String host = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getHostString();
        return Mono.just(host);
    }
}
