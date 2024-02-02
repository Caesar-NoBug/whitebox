package org.caesar.filter;

import org.caesar.common.context.ContextHolder;
import org.caesar.common.log.LogUtil;
import org.caesar.config.RateLimiterConfig;
import org.caesar.config.RateLimiterProperties;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.caesar.util.ExchangeUtil;
import org.redisson.api.RRateLimiterReactive;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

//处理基本的限流操作
//TODO： 熔断处理器, 用nacos加sentinel进行限流
//TODO: 限制验证码访问频率
@Component
public class IdRateLimiterFilter implements GlobalFilter, Ordered {

    // 默认限流器过期时间
    private final int DEFAULT_LIMITER_EXPIRE = 60;

    @Resource
    private RedissonReactiveClient redissonClient;

    @Resource
    private RateLimiterProperties rateLimiterProperties;

    private final Response<Void> FREQUENT_ACCESS_RESPONSE = Response.error(ErrorCode.TOO_MUCH_REQUEST_ERROR,
            "Access Restricted: Your access has been restricted due to frequent access. Please wait a minutes");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String uri = ContextHolder.getBusinessName();
        Long userId = ContextHolder.getUserId();

        return getIdRateLimiter(uri, userId).flatMap(
                rateLimiter -> rateLimiter.tryAcquire().flatMap(
                        acquired -> {
                            if (acquired) return chain.filter(exchange);
                            else {
                                return handleFrequentAccess(rateLimiter)
                                        .then(ExchangeUtil.returnError(exchange, FREQUENT_ACCESS_RESPONSE));
                            }
                        }));
    }

    private Mono<RRateLimiterReactive> getIdRateLimiter(String uri, Long userId) {
        // id限流器的key的前缀
        String ID_RATE_LIMITER = "gateway:idRateLimiter:";
        RRateLimiterReactive rateLimiter = redissonClient.getRateLimiter(ID_RATE_LIMITER + userId);

        return rateLimiter.isExists().flatMap(isExists -> {
            if (isExists) return Mono.just(rateLimiter);

            return rateLimiter.trySetRate(RateType.OVERALL, getRate(uri), 1, RateIntervalUnit.MINUTES)
                    .then(rateLimiter.expire(DEFAULT_LIMITER_EXPIRE, TimeUnit.MINUTES))
                    .thenReturn(rateLimiter);
        });
    }

    private int getRate(String uri) {
        RateLimiterConfig config = rateLimiterProperties.getUriConfig(uri);

        // 默认访问频率
        int DEFAULT_ACCESS_FREQUENCY = 40;
        if(Objects.isNull(config) || config.getUser() == null) return DEFAULT_ACCESS_FREQUENCY;

        return config.getUser();
    }

    // 如果频繁访问则放入黑名单,10分钟内每分钟只允许访问2次，如果仍在频繁访问则持续刷新黑名单时间
    private Mono<Boolean> handleFrequentAccess(RRateLimiterReactive rateLimiter) {
        LogUtil.warn(ErrorCode.TOO_MUCH_REQUEST_ERROR, "Frequent request from user.");
        // 限制访问频率
        int RESTRICTED_ACCESS_FREQUENCY = 2;
        // 限制限流器过期时间
        int RESTRICTED_LIMITER_EXPIRE = 10;
        return rateLimiter.setRate(RateType.OVERALL, RESTRICTED_ACCESS_FREQUENCY, 1, RateIntervalUnit.MINUTES)
                .then(rateLimiter.expire(RESTRICTED_LIMITER_EXPIRE, TimeUnit.MINUTES));
    }

    @Override
    public int getOrder() {
        return -9;
    }

}
