package org.caesar.filter;

import org.caesar.constant.RedisKey;
import org.caesar.domain.constant.Headers;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.redis.RedisCache;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

//TODO: 幂等过滤器，通过请求头的request id来来判断请求是否为重复请求
//@Component
public class IdempotentFilter implements GlobalFilter, Ordered {

    @Resource
    private RedisCache redisCache;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String requestId = exchange.getRequest().getHeaders().getFirst(Headers.REQUEST_ID_HEADER);

        //TODO: 加一个分布式锁来保证只调用1次接口
        //若requestId不为null，即为幂等接口
        if(!Objects.isNull(requestId)) {

            String userId = exchange.getRequest().getHeaders().getFirst(Headers.USERID_HEADER);
            String redisKey = String.format(RedisKey.IDEMPOTENT_REQUEST_ID_KEY, userId, requestId);

            //尚未执行该请求,则放行
            if(Objects.isNull(redisCache.getCacheObject(redisKey))){
                redisCache.setCacheObject(redisKey, true, 1, TimeUnit.MINUTES);
                return chain.filter(exchange);
            }

            //否则，直接抛出异常，返回
            throw new BusinessException(ErrorCode.DUPLICATE_REQUEST, "请求失败，请勿重复请求");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -7;
    }
}
