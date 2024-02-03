package org.caesar.common.batch;

import org.caesar.common.cache.CacheRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class CacheBatchTaskHandler extends BatchTaskHandler {

    public static final long DEFAULT_INTERVAL = 1000;

    @Resource
    private CacheRepository cacheRepo;

    @Override
    public void batchExecute() {
        Map<String, BatchTask> tasks = getTasks();

        tasks.forEach((id, task) -> {
            cacheRepo.incrLong(id);
        });

        tasks.clear();
    }

    @Override
    public long getExecuteInterval() {
        return DEFAULT_INTERVAL;
    }
}
