package org.caesar.service;

import org.caesar.domain.article.request.AddCommentRequest;
import org.caesar.domain.article.request.GetCommentRequest;
import org.caesar.domain.article.vo.CommentVO;
import org.caesar.model.po.CommentPO;
import com.baomidou.mybatisplus.extension.service.IService;

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
    List<CommentVO> getComment(GetCommentRequest request);

    // 评论点赞
    //TODO: 不允许用户短时间多次点赞或点踩，设置一个限流,前端做一下幂等处理，一次对话的requestId固定
    void likeComment(long userId, long commentId);

    // 评论点踩
    void dislikeComment(long userId, long commentId);

    // 删除评论
    void deleteComment(long userId, long commentId);
}
