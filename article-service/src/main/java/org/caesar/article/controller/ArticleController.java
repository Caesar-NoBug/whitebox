package org.caesar.article.controller;

import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.idempotent.Idempotent;
import org.caesar.common.log.Logger;
import org.caesar.domain.article.request.ArticleActionRequest;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.article.request.AddArticleRequest;
import org.caesar.domain.article.request.UpdateArticleRequest;
import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.article.service.ArticleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/article")
@Validated
public class ArticleController {

    @Resource
    private ArticleService articleService;

    // 添加文章
    @Logger(value = "/addArticle", args = true, result = true)
    @PostMapping
    Response<Void> addArticle(@RequestBody AddArticleRequest request) {
        articleService.addArticle(ContextHolder.getUserIdNecessarily(), request);
        return Response.ok();
    }

    @GetMapping("/prefer")
    @Logger(value = "/getPreferArticle", args = true, result = true)
    Response<GetPreferArticleResponse> getPreferArticle(
            @Min(0) @RequestParam Integer viewedSize,
            @Min(0) @RequestParam Integer preferredSize,
            @Min(0) @RequestParam Integer randPreferredSize) {
        long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(articleService.getPreferArticle(userId, viewedSize, preferredSize, randPreferredSize));
    }

    // 查看文章
    @GetMapping("/detail/{articleId}")
    Response<ArticleVO> viewArticle(@PathVariable Long articleId) {
        Long userId = ContextHolder.get(ContextHolder.USER_ID);
        ThrowUtil.ifNull(userId, "unauthenticated user");
        return Response.ok(articleService.viewArticle(ContextHolder.getUserIdNecessarily(), articleId));
    }

    // 查看文章历史
    @GetMapping("/history")
    Response<List<ArticleHistoryVO>> getArticleHistory(@RequestParam Integer from, @RequestParam Integer size) {
        return Response.ok(articleService.getArticleHistory(ContextHolder.getUserIdNecessarily(), from, size));
    }

    // 修改文章
    @PutMapping
    Response<Void> updateArticle(@RequestBody UpdateArticleRequest request) {
        articleService.updateArticle(ContextHolder.getUserIdNecessarily(), request);
        return Response.ok();
    }

    // 删除文章
    @DeleteMapping("/{articleId}")
    Response<Void> deleteArticle(@Min(0) @PathVariable Long articleId) {
        articleService.deleteArticle(ContextHolder.getUserIdNecessarily(), articleId);
        return Response.ok();
    }

    // 评价文章(-1:踩，0:无，1:赞)及收藏文章（true：收藏，false：取消收藏）
    @Logger(value = "article action")
    @Idempotent(value = "article:action", reqId = "#request.id")
    @PutMapping("/action/{articleId}")
    Response<Void> performArticleAction(@Min(0) @PathVariable Long articleId, @RequestBody ArticleActionRequest request) {

        Integer mark = request.getMark();
        Boolean isFavor = request.getIsFavor();

        if(Objects.nonNull(mark))
            articleService.markArticle(ContextHolder.getUserIdNecessarily(), articleId, mark);

        if(Objects.nonNull(isFavor))
            articleService.favorArticle(ContextHolder.getUserIdNecessarily(), articleId, isFavor);

        return Response.ok();
    }

    @PostMapping("/unread")
    Response<List<Long>> getUnreadArticle(@RequestBody List<Long> articleIds) {
        long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(articleService.getUniqueArticle(userId, articleIds));
    }

}
