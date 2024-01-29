package org.caesar.config;

import org.caesar.common.exception.BusinessException;
import org.caesar.domain.common.enums.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExecutorConfig {

    @Value("${executor.coreSize:5}")
    private int coreSize;

    @Value("${executor.maxSize:8}")
    private int maxSize;

    @Value("${executor.queueCapacity:8}")
    private int queueCapacity;

    @Value("${executor.keepAliveTime:60}")
    private int keepAliveTime;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize); // 核心线程数
        executor.setMaxPoolSize(maxSize); // 最大线程数
        executor.setQueueCapacity(queueCapacity); // 队列容量
        executor.setKeepAliveSeconds(keepAliveTime); // 线程的最大空闲时间
        executor.setRejectedExecutionHandler((r, e) -> {
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "Too much connection is holding, fail to create connection.");
        });
        executor.setThreadNamePrefix("[AIGC-Service] Executor-"); // 线程名称前缀

        // 初始化线程池
        executor.initialize();
        return executor;
    }

}
