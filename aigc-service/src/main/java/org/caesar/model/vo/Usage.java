package org.caesar.model.vo;

import lombok.Data;

/**
 * token的使用情况
 */
@Data
public class Usage {

    /**
     * prompt消耗的token数
     */
    private Integer prompt_tokens;

    /**
     * 对话消耗的token数
     */
    private Integer completion_tokens;

    /**
     * 本次对话共消耗的token数
     */
    private Integer total_tokens;
}
