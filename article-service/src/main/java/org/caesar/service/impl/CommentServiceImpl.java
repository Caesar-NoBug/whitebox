package org.caesar.service.impl;

import org.caesar.service.CommentService;
import org.caesar.util.RedisKey;
import org.caesar.common.client.AIGCClient;
import org.caesar.common.client.UserClient;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.repository.CacheRepository;
import org.caesar.common.util.ClientUtil;
import org.caesar.common.vo.Response;
import org.caesar.domain.aigc.request.AnalyseContentRequest;
import org.caesar.domain.aigc.response.AnalyseContentResponse;
import org.caesar.domain.article.request.AddCommentRequest;
import org.caesar.domain.article.request.GetCommentRequest;
import org.caesar.domain.article.vo.CommentVO;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.model.MsCommentStruct;
import org.caesar.model.entity.Comment;
import org.caesar.repository.CommentRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentRepository commentRepo;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private AIGCClient aigcClient;

    @Resource
    private UserClient userClient;

    @Resource
    private MsCommentStruct commentStruct;

    @Override
    public void addComment(long userId, AddCommentRequest request) {
        long id = cacheRepo.nextId(RedisKey.commentIncId());
        Comment comment = Comment.fromAddRequest(id, userId, request);
        Response<AnalyseContentResponse> response = aigcClient.analyseContent(new AnalyseContentRequest("", comment.getContent(), false));
        AnalyseContentResponse analyseResp = ClientUtil.handleResponse(response, "审核评论失败");
        ThrowUtil.ifFalse(analyseResp.isPass(), "评论审核不通过");

        ThrowUtil.ifFalse(commentRepo.addComment(comment), ErrorCode.SYSTEM_ERROR ,"无法添加评论");
    }

    @Override
    public List<CommentVO> getComment(GetCommentRequest request) {
        Integer parentType = request.getParentType();
        Long parentId = request.getParentId();
        Integer from = request.getFrom();
        Integer size = request.getSize();

        List<Comment> comments = commentRepo.getCommentList(parentType, parentId, from, size);

        List<Long> publisherIds = comments.stream()
                .map(Comment::getCreateBy).collect(Collectors.toList());

        Response<Map<Long, UserMinVO>> userMinResp = userClient.getUserMin(publisherIds);

        Map<Long, UserMinVO> publisherMap = ClientUtil.handleResponse(userMinResp, "获取发布者信息失败");

        return comments.stream()
                .map(comment -> loadCommentVO(
                        comment, publisherMap.get(comment.getCreateBy()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public void markComment(long userId, long commentId, int mark) {
        ThrowUtil.ifFalse(commentRepo.markComment(userId, commentId, mark), "评价文章失败");
    }

    @Override
    public void deleteComment(long userId, long commentId) {
        ThrowUtil.ifFalse(commentRepo.deleteComment(userId, commentId), "删除评论失败");
    }

    private CommentVO loadCommentVO(Comment comment, UserMinVO publisher) {

        CommentVO commentVO = cacheRepo.getObject(RedisKey.cacheComment(comment.getId()));

        if(!Objects.isNull(commentVO)) {
            commentVO.setMark(comment.getMark());
            return commentVO;
        }

        commentVO = commentStruct.DOtoVO(comment);

        commentVO.setMark(comment.getMark());

        commentVO.setLikeNum(cacheRepo.getLongValue(RedisKey.commentLikeCount(comment.getId())));

        commentVO.setPublisher(publisher);

        return commentVO;
    }
}




