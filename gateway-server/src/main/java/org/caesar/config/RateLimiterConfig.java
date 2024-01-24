package org.caesar.config;

import org.redisson.api.RRateLimiterReactive;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public RRateLimiterReactive ipRateLimiter(RedissonReactiveClient reactiveClient) {
        RRateLimiterReactive ipRateLimiter = reactiveClient.getRateLimiter("ipRateLimiter");
        ipRateLimiter.setRate(RateType.OVERALL, 10, 1, RateIntervalUnit.SECONDS);
        return ipRateLimiter;
    }

    @Bean
    public RRateLimiterReactive uriRateLimiter(RedissonReactiveClient reactiveClient) {
        RRateLimiterReactive uriRateLimiter = reactiveClient.getRateLimiter("uriRateLimiter");
        uriRateLimiter.setRate(RateType.OVERALL, 10, 1, RateIntervalUnit.SECONDS);
        return uriRateLimiter;
    }

}
