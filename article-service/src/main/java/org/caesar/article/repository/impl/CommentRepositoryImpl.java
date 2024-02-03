package org.caesar.article.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.article.constant.CacheKey;
import org.caesar.common.batch.CacheBatchTaskHandler;
import org.caesar.common.batch.CacheIncTask;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.cache.CacheRepository;
import org.caesar.domain.article.enums.ElementType;
import org.caesar.article.mapper.CommentMapper;
import org.caesar.article.model.MsCommentStruct;
import org.caesar.article.model.entity.Comment;
import org.caesar.article.model.po.CommentPO;
import org.caesar.article.repository.CommentRepository;
import org.caesar.domain.common.enums.ErrorCode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author caesar
 * @description 针对表【comment】的数据库操作Service实现
 * @createDate 2023-11-29 20:36:21
 */
@Repository
public class CommentRepositoryImpl extends ServiceImpl<CommentMapper, CommentPO> implements CommentRepository {

    @Resource
    private MsCommentStruct commentStruct;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private CacheBatchTaskHandler taskHandler;

    @Override
    public void addComment(Comment comment) {
        ThrowUtil.ifFalse(save(commentStruct.DOtoPO(comment)), ErrorCode.SYSTEM_ERROR, "Fail to insert comment into database.");

        cacheRepo.setLongValue(CacheKey.commentLikeCount(comment.getId()), 0);
    }

    @Override
    public List<Comment> getCommentList(int parentType, long parentId, int from, int size) {
        return baseMapper.getCommentList(parentType, parentId, size, from * size)
                .stream()
                .map(commentStruct::POtoDO)
                .collect(Collectors.toList());
    }

    @Override
    public void markComment(long userId, long commentId, int mark) {

        // 如果没有不同的评价（即评价已经是mark了），无需修改
        ThrowUtil.ifFalse(baseMapper.hasDiffCommentMark(userId, commentId, mark), ErrorCode.DUPLICATE_REQUEST, "Comment has already been marked");

        CacheIncTask updateMarkTask = new CacheIncTask(mark);
        taskHandler.addTask(CacheKey.commentLikeCount(commentId), updateMarkTask);

        ThrowUtil.ifFalse(baseMapper.markComment(userId, commentId, mark) > 0, "Fail to mark comment.");
    }

    @Override
    public boolean hasOwnership(long userId, long commentId) {
        return baseMapper.selectCount(
                new QueryWrapper<CommentPO>()
                        .eq("id", commentId)
                        .eq("create_by", userId)
        ) > 0;
    }

    @Transactional
    @Override
    public void deleteComment(long userId, long commentId) {
        ThrowUtil.ifFalse(remove(new QueryWrapper<CommentPO>().eq("id", commentId).eq("create_by", userId)), "评论不存在或无权限删除");

        // 删除评论的点赞记录
        baseMapper.deleteCommentOps(commentId);

        // 删除子评论
        deleteComment(ElementType.COMMENT.getValue(), commentId);
    }

    @Override
    public void deleteComment(int parentType, long parentId) {
        ThrowUtil.ifFalse(remove(
                new QueryWrapper<CommentPO>().eq("parent_type", parentType).eq("parent_id", parentId)
        ), "Comment does not exists or user has no authority to remove it.");
    }

}
