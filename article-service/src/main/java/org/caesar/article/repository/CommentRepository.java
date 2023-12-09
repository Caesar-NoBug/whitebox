package org.caesar.article.repository;

import org.caesar.article.model.entity.Comment;

import java.util.List;

public interface CommentRepository {

    // 发布评论
    boolean addComment(Comment request);

    // 查看评论(包含用户点赞情况)
    List<Comment> getCommentList(int parentType, long parentId, int from, int size);

    // 评论点赞
    boolean markComment(long userId, long commentId, int mark);

    // 评论是否属于用户
    boolean hasOwnership(long userId, long commentId);

    // 删除评论
    boolean deleteComment(long userId, long commentId);

    // 删除删除该父级元素（评论、文章、题目）下的所有评论
    boolean deleteComment(int parentType, long parentId);
}
