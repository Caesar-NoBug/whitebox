package org.caesar.filter;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.log.LogUtil;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//处理基本的限流操作
//TODO： 熔断处理器, 用nacos加sentinel进行限流
//TODO: 限制验证码访问频率
@Component
public class IDRateLimiterFilter implements GlobalFilter, Ordered {

    private final String ID_RATE_LIMITER = "gateway:idRateLimiter:";

    private final String ID_BLACK_LIST = "gateway:idBlackList";

    private final String URI_RATE_LIMITER = "uriRateLimiter:";

    @Resource
    private RedissonReactiveClient redissonClient;

    private final Response<Void> FREQUENT_ACCESS_RESPONSE = Response.error(ErrorCode.TOO_MUCH_REQUEST_ERROR,
            "Access Restricted: Your access has been restricted due to frequent access. Please wait a minutes");

    private final Set<Long> idBlackList = new HashSet<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Long userId = ContextHolder.getUserId();

        if (idBlackList.contains(userId))
            return ExchangeUtil.returnError(exchange, FREQUENT_ACCESS_RESPONSE);

        return getIdRateLimiter(userId).flatMap(
                rateLimiter -> rateLimiter.tryAcquire().flatMap(
                        acquired -> {
                            if (acquired) return chain.filter(exchange);
                            else {
                                handleFrequentAccess(rateLimiter);
                                return ExchangeUtil.returnError(exchange, FREQUENT_ACCESS_RESPONSE);
                            }
                        }));
    }

    private Mono<RRateLimiterReactive> getIdRateLimiter(Long userId) {
        RRateLimiterReactive rateLimiter = redissonClient.getRateLimiter(ID_RATE_LIMITER + userId);

        return rateLimiter.isExists().flatMap(isExists -> {
            if (isExists) return Mono.just(rateLimiter);

            return rateLimiter.trySetRate(RateType.OVERALL, 30, 1, RateIntervalUnit.MINUTES)
                    .thenReturn(rateLimiter);
        });
    }

    // 如果频繁访问则放入黑名单,10分钟内每分钟只允许访问一次，如果仍在频繁访问则持续刷新黑名单时间
    private Mono<Boolean> handleFrequentAccess(RRateLimiterReactive rateLimiter) {
        LogUtil.warn(ErrorCode.TOO_MUCH_REQUEST_ERROR, "Frequent request from user.");
        return rateLimiter.setRate(RateType.OVERALL, 1, 1, RateIntervalUnit.MINUTES)
                .then(rateLimiter.expire(10, TimeUnit.MINUTES));
    }

    private Mono<RRateLimiterReactive> getUriRateLimiter(String key) {

        RRateLimiterReactive rateLimiter = redissonClient.getRateLimiter(URI_RATE_LIMITER + key);

        return rateLimiter.isExists().flatMap(isExists -> {
            if (isExists) return Mono.just(rateLimiter);

            return rateLimiter.trySetRate(RateType.OVERALL, 1000, 1, RateIntervalUnit.SECONDS)
                    .thenReturn(rateLimiter);
        });
    }

    @Override
    public int getOrder() {
        return -9;
    }

}
