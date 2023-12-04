package org.caesar.domain.aigc.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
public class CompletionRequest {

    /**
     * 对话id，若为空表示创建新对话，否则保留聊天历史
     */
    private String id;

    /**
     * 模型选择
     */
    private String model;

    /**
     * 模型预设定
     */
    private String preset;

    /**
     * 用户输入内容
     */
    private String prompt;

    /**
     * 温度，范围在[0, 2]，默认值为1，越高模型想象力越高，越低则模型越严谨
     */
    @Min(0)
    @Max(2)
    private Integer temperature;

    /**
     * 保留历史条数（默认为0，范围在[0 ~ 20]）
     */
    @Min(0)
    @Max(20)
    private Integer memory = 0;

    public CompletionRequest(String preset, String prompt) {
        this.preset = preset;
        this.prompt = prompt;
    }

}
