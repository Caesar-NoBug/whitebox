package org.caesar.domain.search.vo;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class QuestionIndexVO implements IndexVO {

    /**
     * 问题主键
     */
    private Long id;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 问题标签
     */
    private String tag;

    /**
     * 问题难度
     */
    private Integer difficulty;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 收藏数
     */
    private Integer favorNum;

    /**
     * 通过数
     */
    private Integer passNum;

    /**
     * 提交数
     */
    private Integer submitNum;
}
