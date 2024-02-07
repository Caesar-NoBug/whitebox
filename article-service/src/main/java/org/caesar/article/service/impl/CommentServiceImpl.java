package org.caesar.article.service.impl;

import org.caesar.article.service.CommentService;
import org.caesar.article.constant.CacheKey;
import org.caesar.common.client.AIGCClient;
import org.caesar.common.client.UserClient;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.resp.RespUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.caesar.domain.article.request.AddCommentRequest;
import org.caesar.domain.article.request.GetCommentRequest;
import org.caesar.domain.article.vo.CommentVO;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.article.model.MsCommentStruct;
import org.caesar.article.model.entity.Comment;
import org.caesar.article.repository.CommentRepository;
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
        long id = cacheRepo.nextId(CacheKey.commentIncId());
        Comment comment = Comment.fromAddRequest(id, userId, request);

        // 过滤Html文本，以防止XSS
        comment.filterHtml();

        Response<AnalyseTextResponse> response = aigcClient.analyseText(new AnalyseTextRequest("", comment.getContent(), false));
        AnalyseTextResponse analyseResp = RespUtil.handleWithThrow(response, "Fail to review the comment.");
        ThrowUtil.ifFalse(analyseResp.isPass(), "The comment did not pass the review.");

        commentRepo.addComment(comment);
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

        Map<Long, UserMinVO> publisherMap = RespUtil.handleWithThrow(userMinResp, "Fail to fetch author info from user-service.");

        return comments.stream()
                .map(comment -> loadCommentVO(
                        comment, publisherMap.get(comment.getCreateBy()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public void markComment(long userId, long commentId, int mark) {
        commentRepo.markComment(userId, commentId, mark);
    }

    @Override
    public void deleteComment(long userId, long commentId) {
        commentRepo.deleteComment(userId, commentId);
    }

    private CommentVO loadCommentVO(Comment comment, UserMinVO publisher) {

        //TODO: 评论的缓存处理

        CommentVO commentVO = cacheRepo.getObject(CacheKey.cacheComment(comment.getId()));

        if(!Objects.isNull(commentVO)) {
            commentVO.setMark(comment.getMark());
            return commentVO;
        }

        commentVO = commentStruct.DOtoVO(comment);

        commentVO.setMark(comment.getMark());

        commentVO.setLikeNum(cacheRepo.getLongValue(CacheKey.commentLikeCount(comment.getId())));

        commentVO.setPublisher(publisher);

        return commentVO;
    }
}




