package org.caesar.common.batch.cache;

import org.caesar.common.batch.BatchTask;
import org.caesar.common.batch.BatchTaskHandler;
import org.caesar.common.cache.CacheRepository;

import javax.annotation.Resource;
import java.util.Map;

public class CacheIncTaskHandler extends BatchTaskHandler {

    public CacheIncTaskHandler(CacheRepository cacheRepository, long executeInterval) {
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
