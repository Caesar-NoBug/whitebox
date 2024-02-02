package org.caesar.article.model.entity;

import lombok.Data;
import org.caesar.domain.user.vo.UserMinVO;

import java.time.LocalDateTime;

@Data
public class ArticleHistory {
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
     *  作者信息
     */
    private Long createBy;

    /**
     * 浏览时间
     */
    private LocalDateTime viewAt;
}
