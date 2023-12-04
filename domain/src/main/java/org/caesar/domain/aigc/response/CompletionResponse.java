package org.caesar.domain.aigc.response;

import lombok.Data;

@Data
public class CompletionResponse {

    /**
     * 对话id，用于保留聊天历史
     */
    private String id;

    /**
     * 模型回复的内容
     */
    private String reply;
}
