package org.caesar.filter;

import org.caesar.common.context.ContextHolder;
import org.caesar.config.RateLimiterConfig;
import org.caesar.config.RateLimiterProperties;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.caesar.util.ExchangeUtil;
import org.redisson.api.*;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class UriRateLimiter implements GlobalFilter, Ordered {

    // uri限流器的key的前缀
    private final String URI_RATE_LIMITER = "uriRateLimiter:";

    // 默认访问频率
    private final int DEFAULT_ACCESS_FREQUENCY = 100;

    // 默认限流器过期时间
    private final int DEFAULT_LIMITER_EXPIRE = 24;

    @Resource
    private RateLimiterProperties rateLimiterProperties;

    @Resource
    private RedissonReactiveClient redissonReactiveClient;

    private final Response<Void> FREQUENT_ACCESS_RESPONSE = Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR,
            "Access Restricted: Service unavailable due to frequent access. Please wait a seconds.");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String uri = ContextHolder.getBusinessName();

        return getUriRateLimiter(uri).flatMap(
                rateLimiter -> rateLimiter.tryAcquire().flatMap(
                        acquired -> {
                            if (acquired)
                                return chain.filter(exchange);
                            else
                                return ExchangeUtil.returnError(exchange, FREQUENT_ACCESS_RESPONSE);
                        }));
    }

    private Mono<RRateLimiterReactive> getUriRateLimiter(String uri) {

        RRateLimiterReactive rateLimiter = redissonReactiveClient.getRateLimiter(URI_RATE_LIMITER + uri);

        return rateLimiter.isExists().flatMap(isExists -> {
            if (isExists) return Mono.just(rateLimiter);

            return rateLimiter.trySetRate(RateType.OVERALL, getRate(uri), 1, RateIntervalUnit.SECONDS)
                    .then(rateLimiter.expire(DEFAULT_LIMITER_EXPIRE, TimeUnit.HOURS))
                    .thenReturn(rateLimiter);
        });
    }

    private int getRate(String uri) {
        RateLimiterConfig config = rateLimiterProperties.getUriConfig(uri);

        if(Objects.isNull(config) || config.getTotal() == null) return DEFAULT_ACCESS_FREQUENCY;

        return config.getTotal();
    }

    @Override
    public int getOrder() {
        return -9;
    }

}