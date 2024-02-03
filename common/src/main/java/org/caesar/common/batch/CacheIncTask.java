package org.caesar.common.batch;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CacheIncTask implements BatchTask{

    private int increment;

    @Override
    public BatchTask merge(BatchTask task) {

        if(task instanceof CacheIncTask)
            this.increment += ((CacheIncTask)task).getIncrement();

        return this;
    }

}
