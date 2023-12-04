package org.caesar.model.entity;

import lombok.Data;


@Data
public class Comment{

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

}