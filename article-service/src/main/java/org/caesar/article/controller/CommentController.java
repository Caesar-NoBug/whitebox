package org.caesar.article.controller;

import io.swagger.annotations.Api;
import org.caesar.article.service.CommentService;
import org.caesar.common.context.ContextHolder;
import org.caesar.domain.article.request.AddCommentRequest;
import org.caesar.domain.article.request.GetCommentRequest;
import org.caesar.domain.article.vo.CommentVO;
import org.caesar.domain.common.vo.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/comment")
@Validated
@Api("评论服务")
public class CommentController {

    @Resource
    private CommentService commentService;

    // 发布评论
    @PostMapping
    Response<Void> addComment(@RequestBody AddCommentRequest request) {
        long userId = ContextHolder.getUserIdNecessarily();
        commentService.addComment(userId, request);
        return Response.ok();
    }

    // 查看评论
    @GetMapping("/list")
    Response<List<CommentVO>> getComment(@Min(0) @RequestParam Integer parentType,
                               @Min(0) @RequestParam Long parentId,
                               @Min(0) @RequestParam Integer from,
                               @Min(0) @RequestParam Integer size) {
        return Response.ok(commentService.getComment(parentType, parentId, from, size));
    }

    // 评价评论（-1：踩，0：取消赞或踩，1：赞）
    @PutMapping("/mark/{commentId}")
    Response<Void> markComment(@PathVariable Long commentId, @RequestParam Integer mark) {
        long userId = ContextHolder.getUserIdNecessarily();
        commentService.markComment(userId, commentId, mark);
        return Response.ok();
    }

    // 删除评论
    @DeleteMapping("ops/{commentId}")
    Response<Void> deleteComment(@PathVariable Long commentId) {
        long userId = ContextHolder.getUserIdNecessarily();
        commentService.deleteComment(userId, commentId);
        return Response.ok();
    }
}
