package org.caesar.model;

import org.caesar.domain.article.vo.CommentVO;
import org.caesar.model.entity.Comment;
import org.caesar.model.po.CommentPO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsCommentStruct {

    Comment POtoDO(CommentPO commentPO);
    CommentPO DOtoPO(Comment comment);
    CommentVO DOtoVO(Comment comment);
}