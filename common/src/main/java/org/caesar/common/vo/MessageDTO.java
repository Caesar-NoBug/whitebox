package org.caesar.common.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.common.context.ContextHolder;

@Data
@NoArgsConstructor
public class MessageDTO<T> {
    private T payload;
    private Long userId;
    private String traceId;

    public MessageDTO(T payload) {
        this.payload = payload;
        this.userId = ContextHolder.getUserId();
        this.traceId = ContextHolder.getTraceId();
    }

    public T getPayload() {
        ContextHolder.setUserId(userId);
        ContextHolder.setTraceId(traceId);
        return payload;
    }

}
