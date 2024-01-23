package org.caesar.common.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.log.LogUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class Resilience4jConfig {

    // 故障率阈值
    @Value("${resilience4j.config.failureRateThreshold:0.5}")
    private float failureRateThreshold;

    // 半开状态的最大等待时间（单位为秒）
    @Value("${resilience4j.config.waitDurationInOpenState:5}")
    private int waitDurationInOpenState;

    // 计算故障率所需最小调用次数
    @Value("${resilience4j.config.minimumNumberOfCalls:100}")
    private int minimumNumberOfCalls;

    // 半开状态允许的试探调用次数
    @Value("${resilience4j.config.permittedNumberOfCallsInHalfOpenState:10}")
    private int permittedNumberOfCallsInHalfOpenState;

    // 滑动窗口大小
    @Value("${resilience4j.config.slidingWindowSize:100}")
    private int slidingWindowSize;

    // 触发熔断的慢调用率阈值
    @Value("${resilience4j.config.showCallRateThreshold:0.5}")
    private float showCallRateThreshold;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {

        CircuitBreakerConfig defaultConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .maxWaitDurationInHalfOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .slidingWindowSize(slidingWindowSize)
                .slowCallRateThreshold(showCallRateThreshold)
                .recordResult(res -> {
                    System.out.println("Circuit Breaker invoked");
                    if(res instanceof Response) {
                        Response response = (Response) res;

                        ErrorCode code = ErrorCode.of(response.getCode());

                        if (Objects.isNull(code)) {
                            LogUtil.error(ErrorCode.SYSTEM_ERROR, "Invalid response error code.");
                            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Invalid response error code.");
                        }

                        if(code.isFatal()) {
                            LogUtil.error(ErrorCode.SYSTEM_ERROR, response.getMsg());
                            throw new BusinessException(code, response.getMsg());
                        }

                        return true;
                    }

                    return true;
                }).build();

        return CircuitBreakerRegistry.of(defaultConfig);
    }
}
