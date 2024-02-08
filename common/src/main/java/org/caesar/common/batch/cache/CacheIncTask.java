package org.caesar.common.batch.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.caesar.common.batch.BatchTask;

@Data
@AllArgsConstructor
public class CacheIncTask implements BatchTask {

    private int increment;

    @Override
    public BatchTask merge(BatchTask task) {

        if(task instanceof CacheIncTask)
            this.increment += ((CacheIncTask)task).getIncrement();

        return this;
    }

}
