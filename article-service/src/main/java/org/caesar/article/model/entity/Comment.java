package org.caesar.article.model.entity;

import cn.hutool.http.HtmlUtil;
import lombok.Data;
import org.caesar.domain.article.request.AddCommentRequest;


@Data
public class Comment{

    /**
     * 评论主键
     */
    private Long id;

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

    /**
     * 发布者id
     */
    private Long createBy;

    /**
     * 点赞数
     */
    private Long likeNum;

    /**
     *  评价（-1：踩，0：无，1：赞）
     */
    private Integer mark;

    public static Comment fromAddRequest(long id, long authorId ,AddCommentRequest request) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setParentType(request.getParentType());
        comment.setId(id);
        comment.setLikeNum(0L);
        comment.setCreateBy(authorId);
        return comment;
    }

    public void filterHtml() {
        content = HtmlUtil.filter(content);
    }

}