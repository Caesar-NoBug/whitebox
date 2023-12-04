package org.caesar.domain.article.vo;

import lombok.Data;
import org.caesar.domain.user.vo.UserMinVO;

import java.time.LocalDateTime;

/**
 *  文章详情VO
 */
@Data
public class ArticleVO {

    /**
     * 文章主键
     */
    private Long id;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章标签
     */
    private String tags;

    /**
     * 收藏数
     */
    private Integer favorNum;

    /**
     * 浏览数
     */
    private Integer viewNum;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 创建者信息
     */
    private UserMinVO author;

    /**
     * 评价（-1：踩，0：无，1：赞）
     */
    private int mark;

    /**
     * 是否已收藏
     */
    private boolean favored;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    private LocalDateTime updateAt;
}
