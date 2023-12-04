package org.caesar.domain.aigc.request;

import lombok.Data;

@Data
public class AnalyseArticleRequest {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     *  是否生成文章摘要、标签
     */
    private boolean genContent;

}
