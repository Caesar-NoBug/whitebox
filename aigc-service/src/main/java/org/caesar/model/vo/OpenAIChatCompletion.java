package org.caesar.model.vo;

import lombok.Data;
import org.caesar.domain.aigc.request.CompletionRequest;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 对话信息存储类
 */
@Data
public class OpenAIChatCompletion implements Serializable {

    /**
     * 消息历史(由两部分组成，第一项为模型预设、第二项为历史记录)
     */
    private List<Message> messages;

    /**
     * 聊天模型
     */
    private String model;

    /**
     * 温度，范围在[0, 2]，默认值为1，越高模型想象力越高，越低则模型越严谨
     */
    private Integer temperature;

    /**
     * 保留历史条数（默认为0，范围在[0 ~ 20]）
     */
    private Integer memory;

    /**
     * 对话消耗的token数
     */
    private Integer tokens;

    // 构造函数(根据请求构造对话信息)
    public OpenAIChatCompletion(CompletionRequest request) {

        Message preset = Message.preset(request.getPreset());
        List<Message> messages = new LinkedList<>();
        messages.add(preset);
        messages.add(Message.prompt(request.getPrompt()));
        this.tokens = 0;
        this.messages = messages;
        this.model = request.getModel();
        this.temperature = request.getTemperature();
        this.memory = request.getMemory();
    }

    //  根据响应更新对话信息
    public void update(OpenAIChatResponse response) {

        //  响应消息
        Message reply = response.getChoices().get(0).getMessage();

        //  更新对话信息
        this.tokens = this.tokens + response.getUsage().getTotal_tokens();

        //  删除超出记忆范围的历史记录
        if (this.messages.size() > memory)
            this.messages.remove(1);

        //  添加回复消息
        this.messages.add(reply);

    }

}
