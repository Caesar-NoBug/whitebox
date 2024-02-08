package org.caesar.question.model.entity;

import lombok.Data;

@Data
public class QuestionOps {

    /**
     * 评价（-1：踩，0：无，1：赞）
     */
    private int mark;

    /**
     * 是否已收藏
     */
    private boolean favored;
}
