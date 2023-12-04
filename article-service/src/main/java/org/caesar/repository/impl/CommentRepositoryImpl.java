package org.caesar.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.article.enums.ElementType;
import org.caesar.mapper.CommentMapper;
import org.caesar.model.MsCommentStruct;
import org.caesar.model.entity.Comment;
import org.caesar.model.po.CommentPO;
import org.caesar.repository.CommentRepository;
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

    @Override
    public boolean addComment(Comment comment) {
        return save(commentStruct.DOtoPO(comment));
    }

    @Override
    public List<Comment> getCommentList(int parentType, long parentId, int from, int size) {
        return baseMapper.getCommentList(parentType, parentId, size, from * size)
                .stream()
                .map(commentStruct::POtoDO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean markComment(long userId, long commentId, int mark) {

        // 如果没有不同的评价（即评价已经是mark了），无需修改
        if(!baseMapper.hasDiffCommentMark(userId, commentId, mark)) return false;

        return baseMapper.markComment(userId, commentId, mark) > 0;
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
    public boolean deleteComment(long userId, long commentId) {
        ThrowUtil.ifFalse(remove(new QueryWrapper<CommentPO>().eq("id", commentId).eq("create_by", userId)), "评论不存在或无权限删除");

        // 删除评论的点赞记录
        baseMapper.deleteCommentOps(commentId);

        // 删除子评论
        deleteComment(ElementType.COMMENT.getValue(), commentId);
        return true;
    }

    @Override
    public boolean deleteComment(int parentType, long parentId) {
        return remove(
                new QueryWrapper<CommentPO>().eq("parent_type", parentType).eq("parent_id", parentId)
        );
    }

}
