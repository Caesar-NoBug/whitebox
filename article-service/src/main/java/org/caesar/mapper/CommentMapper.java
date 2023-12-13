package org.caesar.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.caesar.model.po.CommentPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author caesar
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2023-11-29 20:36:21
* @Entity org.caesar.model.entity.Comment
*/
@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {

    List<CommentPO> getCommentList(int parentType, long parentId, int size, int offset);

    /**
     * @param userId    用户id
     * @param commentId 评论id
     * @param mark      评价
     * @return          是否有评价不同的评论(有则返回1，没有则返回0)
     */
    boolean hasDiffCommentMark(long userId, long commentId, int mark);

    /**
     * @param userId 用户id
     * @param commentId 文章id
     * @param mark 是否喜欢，-1：不喜欢，0：中等，1：喜欢
     */
    int markComment(long userId, long commentId, int mark);

    int deleteCommentOps(long commentId);


}




