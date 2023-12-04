package org.caesar.domain.article.request;

import lombok.Data;

@Data
public class AddArticleRequest {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String digest;

    /**
     * 文章标签
     */
    private String tags;

    /**
     *  是否使用ai生成摘要、标签
     */
    private boolean genContent;

}
