package org.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.domain.article.request.AddCommentRequest;
import org.caesar.domain.article.request.GetCommentRequest;
import org.caesar.domain.article.vo.CommentVO;
import org.caesar.model.po.CommentPO;
import org.caesar.service.CommentService;
import org.caesar.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentServiceImpl implements CommentService{

    @Override
    public void addComment(long userId, AddCommentRequest request) {

    }

    @Override
    public List<CommentVO> getComment(GetCommentRequest request) {
        return null;
    }

    @Override
    public void likeComment(long userId, long commentId) {

    }

    @Override
    public void dislikeComment(long userId, long commentId) {

    }

    @Override
    public void deleteComment(long userId, long commentId) {

    }
}




