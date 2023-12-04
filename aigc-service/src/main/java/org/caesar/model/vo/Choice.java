package org.caesar.model.vo;

import lombok.Data;

/**
 * 响应结果
 */
@Data
public class Choice {

    /**
     * 返回的消息
     */
    private Message message;

    /**
     * 返回的原因
     */
    private String finish_reason;

    /**
     * 本条消息的编号
     */
    private int index;
}
