package org.caesar.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OpenAIChatRequest implements ChatRequest{

    /**
     * 发出的消息
     */
    private List<Message> messages = new ArrayList<>();

    /**
     * 聊天模型
     */
    private String model = "gpt-3.5-turbo";

    /**
     * -2 ~ 2 越高模型重复率越低
     */
    private Integer frequency_penalty;

    /**
     * 允许本次对话消耗的最大token数，默认为无限制
     */
    private Integer max_tokens;

    /**
     *  -2 ~ 2 越高模型模型谈论新主题的可能性越高
     */
    private  Integer presence_penalty;

    /**
     * 是否流式输出,默认为false
     */
    private Boolean stream;

    /**
     * 温度，范围在[0, 2]，默认值为1，越高模型想象力越高，越低则模型越严谨
     */
    private Integer temperature;

    //  根据对话信息构造请求
    public OpenAIChatRequest(OpenAIChatCompletion completion) {
        this.messages = completion.getMessages();

        if(completion.getModel()!= null)
            this.model = completion.getModel();

        this.temperature = completion.getTemperature();
    }
}
