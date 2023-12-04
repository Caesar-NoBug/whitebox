package org.caesar.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 聊天响应结果
 */
@Data
public class OpenAIChatResponse implements ChatResponse{

    /**
     * 聊天id
     */
    private String id;

    /**
     *
     */
    private String object;

    /**
     * 创建时间
     */
    private Long created;

    /**
     * 对话模型
     */
    private String model;

    /**
     * token使用情况
     */
    private Usage usage;

    /**
     * 响应结果
     */
    private List<Choice> choices;
}
