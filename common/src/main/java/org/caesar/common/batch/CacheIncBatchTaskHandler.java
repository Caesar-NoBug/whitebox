package org.caesar.common.batch;

import org.caesar.common.cache.CacheRepository;

import javax.annotation.Resource;
import java.util.Map;

public class CacheIncBatchTaskHandler extends BatchTaskHandler {

    public CacheIncBatchTaskHandler(CacheRepository cacheRepository, long executeInterval) {
        this.cacheRepo = cacheRepository;
        this.executeInterval = executeInterval;
    }

    public long executeInterval;

    @Resource
    private CacheRepository cacheRepo;

    @Override
    public void batchExecute() {
        Map<String, BatchTask> tasks = getTasks();

        tasks.forEach((id, task) -> {
            CacheIncTask cacheIncTask = (CacheIncTask) task;
            cacheRepo.incrLong(id, cacheIncTask.getIncrement());
        });

        tasks.clear();
    }

    @Override
    public long getExecuteInterval() {
        return executeInterval;
    }
}
