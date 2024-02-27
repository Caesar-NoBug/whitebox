package org.caesar.domain.article.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

@Data
public class UpdateArticleRequest {

    /**
     * 文章标题
     */
    @Length(max = 100)
    private String title;

    /**
     * 文章内容
     */
    @Length(max = 100000)
    private String content;

    /**
     * 文章摘要
     */
    @Length(max = 200)
    private String digest;

    /**
     * 文章标签
     */
    @Length(max = 100)
    private String tags;
}
