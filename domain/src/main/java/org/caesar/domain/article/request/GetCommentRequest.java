package org.caesar.domain.article.request;

import lombok.Data;

@Data
public class GetCommentRequest {

    /**
     * 父对象类型，type取值：0，文章；1，评论；2，题目
     */
    private Integer parentType;

    /**
     * 父对象id
     */
    private Long parentId;

    /**
     * 页码
     */
    private Integer from;

    /**
     * 页大小
     */
    private Integer size;
}
