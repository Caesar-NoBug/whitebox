package org.caesar.article.controller;

import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.vo.Response;
import org.caesar.domain.article.request.AddArticleRequest;
import org.caesar.domain.article.request.UpdateArticleRequest;
import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.article.service.ArticleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    // 添加文章
    Response<Void> addArticle(AddArticleRequest request) {
        articleService.addArticle(ContextHolder.getUserId(), request);
        return Response.ok();
    }

    // 查看文章
    Response<ArticleVO> viewArticle(long articleId) {
        Long userId = ContextHolder.get(ContextHolder.USER_ID);
        ThrowUtil.ifNull(userId, "用户未登录");
        return Response.ok(articleService.viewArticle(ContextHolder.getUserId(), articleId));
    }

    // 查看文章历史
    Response<List<ArticleHistoryVO>> getArticleHistory(Integer from, Integer size) {
        return Response.ok(articleService.getArticleHistory(ContextHolder.getUserId(), from, size));
    }

    // 修改文章
    Response<Void> updateArticle(UpdateArticleRequest request) {
        articleService.updateArticle(ContextHolder.getUserId(), request);
        return Response.ok();
    }

    // 删除文章
    Response<Void> deleteArticle(long articleId) {
        articleService.deleteArticle(ContextHolder.getUserId(), articleId);
        return Response.ok();
    }

    // 评价文章(-1:踩，0:无，1:赞)
    Response<Void> markArticle(long articleId, int mark) {
        articleService.markArticle(ContextHolder.getUserId(), articleId, mark);
        return Response.ok();
    }

    // 文章收藏
    Response<Void> favorArticle(long articleId, boolean isFavor) {
        articleService.favorArticle(ContextHolder.getUserId(), articleId, isFavor);
        return Response.ok();
    }

}
