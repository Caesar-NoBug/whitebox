package org.caesar.domain.article.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *  评论VO
 */
@Data
public class CommentVO {

    /**
     * 评论主键
     */
    private Long id;

    /**
     * 父对象类型，type取值：0，文章；1，评论；2，题目
     */
    private Integer parentType;

    /**
     * 父对象id
     */
    private Long parentId;

    /**
     * 评论内容（不超过1024个字符）
     */
    private String content;

    /**
     * 发布者id
     */
    private Long createBy;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 是否已点赞
     */
    private boolean liked;
}
