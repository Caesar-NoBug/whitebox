package org.caesar.common.batch;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BatchTaskManager implements ApplicationContextAware {

    @Resource
    private TaskScheduler taskScheduler;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 将所有的BatchTaskHandler注册为定时任务
        Map<String, BatchTaskHandler> tempMap = applicationContext.getBeansOfType(BatchTaskHandler.class);
        tempMap.values().forEach(handler -> taskScheduler.scheduleAtFixedRate(handler::batchExecute, handler.getExecuteInterval()));
    }
}
