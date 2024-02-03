package org.caesar.common.batch;

import lombok.Data;

public interface BatchTask {
    BatchTask merge(BatchTask task);
}
