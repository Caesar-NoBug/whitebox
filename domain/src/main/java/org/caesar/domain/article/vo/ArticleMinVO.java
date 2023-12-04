package org.caesar.domain.article.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleMinVO {

    /**
     * 文章主键
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String digest;

    /**
     * 浏览数
     */
    private Integer viewNum;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 创建时间
     */
    private LocalDateTime updateAt;
}
