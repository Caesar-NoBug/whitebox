package org.caesar.common.batch;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

// 批量任务处理器
public abstract class BatchTaskHandler {

    private final Map<String, BatchTask> tasks = new ConcurrentHashMap<>();

    public void addTask(String id, BatchTask task) {
        tasks.merge(id, task, BatchTask::merge);
    }

    protected Map<String, BatchTask> getTasks() {
        return tasks;
    }

    // 执行任务
    public abstract void batchExecute();

    // 执行间隔（ms）
    public abstract long getExecuteInterval();
}

