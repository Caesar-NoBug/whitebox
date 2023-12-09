package org.caesar.domain.article.vo;

import lombok.Data;
import org.caesar.domain.user.vo.UserMinVO;

/**
 *  评论VO
 */
@Data
public class CommentVO {

    /**
     * 评论主键
     */
    private long id;

    /**
     * 评论内容（不超过1024个字符）
     */
    private String content;

    /**
     * 发布者信息
     */
    private UserMinVO publisher;

    /**
     * 点赞数
     */
    private long likeNum;

    /**
     * 点赞情况（-1：点踩；0：无；1：点赞）
     */
    private int mark;
}
