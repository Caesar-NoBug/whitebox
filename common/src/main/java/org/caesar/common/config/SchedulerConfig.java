package org.caesar.common.config;

import org.caesar.common.exception.ExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler(ExceptionHandler exceptionHandler) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setErrorHandler(exceptionHandler::handleException);
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("Scheduled-task-");
        scheduler.initialize();
        return scheduler;
    }

}
