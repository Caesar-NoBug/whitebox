package org.caesar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

//@EnableAsync
//@Configuration
//@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolConfig {
//TODO: 重新弄一下线程池
    private Integer corePoolSize;

    private Integer maxPoolSize;

    private Integer keepAliveTime;

    private Integer workQueue;

    public static final String THREAD_PREFIX = "submitExecutor-";

    @Bean
    public Executor submitExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setQueueCapacity(workQueue);
        executor.setThreadNamePrefix(THREAD_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

}
