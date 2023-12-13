package org.caesar.model.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class CommentPO implements Serializable {

    /**
     * 评论主键
     */
    @TableId
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
     * 评价
     */
    private Integer mark;

    /**
     * 逻辑删除字段
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}