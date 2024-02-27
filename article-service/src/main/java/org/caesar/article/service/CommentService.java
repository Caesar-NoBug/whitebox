package org.caesar.article.service;

import org.caesar.domain.article.request.AddCommentRequest;
import org.caesar.domain.article.request.GetCommentRequest;
import org.caesar.domain.article.vo.CommentVO;

import javax.validation.constraints.Min;
import java.util.List;

/**
* @author caesar
* @description 针对表【comment】的数据库操作Service
* @createDate 2023-11-29 20:36:21
*/
public interface CommentService {

    // 发布评论
    void addComment(long userId, AddCommentRequest request);

    // 查看评论
    List<CommentVO> getComment(Integer parentType, Long parentId, Integer from, Integer size);

    // 评价评论（-1：踩，0：取消赞或踩，1：赞）
    void markComment(long userId, long commentId, int mark);

    // 删除评论
    void deleteComment(long userId, long commentId);
}
