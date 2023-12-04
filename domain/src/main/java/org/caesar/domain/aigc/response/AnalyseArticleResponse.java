package org.caesar.domain.aigc.response;

import lombok.Data;

@Data
public class AnalyseArticleResponse {

    /**
     * 文章是否通过审核
     */
    private boolean pass;

    /**
     * 文章摘要
     */
    private String digest;

    /**
     * 文章标签
     */
    private String tags;
}
