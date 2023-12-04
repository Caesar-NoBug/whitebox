package org.caesar.domain.article.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 发布评论请求
 */
@Data
public class AddCommentRequest {

    /**
     * 父对象类型，type取值：0，文章；1，评论；2，题目
     */
    private Integer parentType;

    /**
     * 父对象id
     */
    private Long parentId;

    /**
     * 评论内容（不超过1024个字符）
     */
    private String content;
}
