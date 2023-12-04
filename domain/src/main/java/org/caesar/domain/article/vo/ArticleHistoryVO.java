package org.caesar.domain.article.vo;


import lombok.Data;
import org.caesar.domain.user.vo.UserMinVO;

import java.time.LocalDateTime;

@Data
public class ArticleHistoryVO {

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
    private UserMinVO author;

    /**
     * 浏览时间
     */
    private LocalDateTime viewTime;
}
