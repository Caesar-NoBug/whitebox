package org.caesar.controller;

import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.log.Logger;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.article.request.AddArticleRequest;
import org.caesar.domain.article.request.UpdateArticleRequest;
import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.caesar.domain.article.vo.ArticleHistoryVO;
import org.caesar.domain.article.vo.ArticleVO;
import org.caesar.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/article")
public class ArticleController {
//TODO: 同步更新作者的信息

    @Resource
    private ArticleService articleService;

    // 添加文章
    @Logger(value = "/addArticle", args = true, result = true)
    @PostMapping
    Response<Void> addArticle(@RequestBody AddArticleRequest request) {
        articleService.addArticle(ContextHolder.getUserIdRequired(), request);
        return Response.ok();
    }

    @GetMapping("/prefer")
    @Logger(value = "/getPreferArticle", args = true, result = true)
    Response<GetPreferArticleResponse> getPreferArticle(
            @Min(0) @RequestParam Integer viewedSize,
            @Min(0) @RequestParam Integer preferredSize,
            @Min(0) @RequestParam Integer randPreferredSize) {
        long userId = ContextHolder.getUserIdRequired();
        return Response.ok(articleService.getPreferArticle(userId, viewedSize, preferredSize, randPreferredSize));
    }

    // 查看文章
    @GetMapping("/detail/{articleId}")
    Response<ArticleVO> viewArticle(@PathVariable Long articleId) {
        Long userId = ContextHolder.get(ContextHolder.USER_ID);
        ThrowUtil.ifNull(userId, "unauthenticated user");
        return Response.ok(articleService.viewArticle(ContextHolder.getUserIdRequired(), articleId));
    }

    // 查看文章历史
    @GetMapping("/history")
    Response<List<ArticleHistoryVO>> getArticleHistory(@RequestParam Integer from, @RequestParam Integer size) {
        return Response.ok(articleService.getArticleHistory(ContextHolder.getUserIdRequired(), from, size));
    }

    // 修改文章
    @PutMapping
    Response<Void> updateArticle(@RequestBody UpdateArticleRequest request) {
        articleService.updateArticle(ContextHolder.getUserIdRequired(), request);
        return Response.ok();
    }

    // 删除文章
    @DeleteMapping("/{articleId}")
    Response<Void> deleteArticle(@PathVariable Long articleId) {
        articleService.deleteArticle(ContextHolder.getUserIdRequired(), articleId);
        return Response.ok();
    }

    // 评价文章(-1:踩，0:无，1:赞)及收藏文章（true：收藏，false：取消收藏）
    @PutMapping("/ops/{articleId}")
    Response<Void> markArticle(@PathVariable Long articleId,@RequestParam Integer mark, @RequestParam Boolean isFavor) {

        if(Objects.nonNull(mark))
            articleService.markArticle(ContextHolder.getUserIdRequired(), articleId, mark);

        if(Objects.nonNull(isFavor))
            articleService.favorArticle(ContextHolder.getUserIdRequired(), articleId, isFavor);

        return Response.ok();
    }

    @PostMapping("/unique")
    Response<List<Long>> getUniqueArticle(@RequestBody List<Long> articleIds) {
        long userId = ContextHolder.getUserIdRequired();
        return Response.ok(articleService.getUniqueArticle(userId, articleIds));
    }

}
