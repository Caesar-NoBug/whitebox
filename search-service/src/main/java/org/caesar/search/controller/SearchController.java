package org.caesar.search.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.str.StrUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.search.enums.ArticleSortField;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.QuestionSortField;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.*;
import org.caesar.search.service.SearchHistoryService;
import org.caesar.search.manager.SearchManager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Api(tags = "搜索服务")
public class SearchController {

    @Resource
    private SearchManager searchManager;

    @Resource
    private SearchHistoryService searchHistoryService;

    @ApiOperation("基础搜索")
    @GetMapping("/search/{dataSource}")
    public Response<PageVO<?>> search(@RequestParam String text, @RequestParam int from, @RequestParam int size,
                                                      @RequestParam String field, @PathVariable DataSource dataSource) {
        if (StrUtil.isBlank(field))
            return Response.ok(searchManager.search(dataSource, text, from, size));
        else {
            SortField sortField = getSortField(dataSource, field);
            return Response.ok(searchManager.sortSearch(dataSource, text, sortField, from, size));
        }
    }

    @ApiOperation("批量搜索")
    @GetMapping("/search-batch/{dataSource}")
    Response<List<?>> searchBatch(@RequestParam List<String> texts, @RequestParam int size, @PathVariable DataSource dataSource) {
        return Response.ok(searchManager.searchBatch(texts, size, dataSource));
    }

    // 用于生成前端实体类
    @GetMapping("/articleIndex")
    public ArticleIndexVO articleIndex() {
        return null;
    }

    @GetMapping("/questionIndex")
    public QuestionIndexVO questionIndex() {
        return null;
    }

    @ApiOperation("聚合搜索")
    @GetMapping("/search-aggregation")
    Response<List<AggregationPageVO<?>>> searchAggregation(@RequestParam String text,
                                                  @RequestParam int from,
                                                  @RequestParam int size) {
        return Response.ok(searchManager.searchAggregation(text, from, size));
    }

    @ApiOperation("搜索建议")
    @GetMapping("/suggestion")
    public Response<List<String>> suggestion(DataSource dataSource, String text, int size) {
        return Response.ok(searchManager.suggestion(dataSource, text, size));
    }

    @ApiOperation("同步问题索引")
    @PostMapping("/sync/question-index")
    public Response<Void> syncQuestionIndex(@RequestBody List<QuestionIndexVO> indices) {
        searchManager.insertIndex(DataSource.QUESTION, indices);
        return Response.ok();
    }

    @ApiOperation("同步文章索引")
    @PostMapping("/sync/article-index")
    public Response<Void> syncArticleIndex(@RequestBody List<ArticleIndexVO> indices) {
        searchManager.insertIndex(DataSource.ARTICLE, indices);
        return Response.ok();
    }

    @ApiOperation("删除索引")
    @DeleteMapping("/sync/{dataSource}")
    public Response<Void> deleteIndex(@RequestBody List<Long> ids, @PathVariable DataSource dataSource) {
        searchManager.deleteIndex(dataSource, ids);
        return Response.ok();
    }

    @ApiOperation("获取搜索历史")
    @GetMapping("/history")
    public Response<List<SearchHistoryVO>> getSearchHistory(@RequestParam Integer size, @RequestParam DataSource dataSource) {
        long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(searchHistoryService.getSearchHistory(userId, size, dataSource));
    }

    private SortField getSortField(DataSource dataSource, String field) {
        SortField sortField = null;
        switch (dataSource) {
            case QUESTION:
                sortField = QuestionSortField.of(field);
                break;
            case ARTICLE:
                sortField = ArticleSortField.of(field);
                break;
        }

        ThrowUtil.ifNull(sortField, ErrorCode.INVALID_ARGS_ERROR, "Invalid sort field: " + field);

        return sortField;
    }

}
