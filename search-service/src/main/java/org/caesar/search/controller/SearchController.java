package org.caesar.search.controller;

import org.caesar.common.context.ContextHolder;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.ArticleIndexVO;
import org.caesar.domain.search.vo.IndexVO;
import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.domain.search.vo.SearchHistoryVO;
import org.caesar.domain.common.vo.PageVO;
import org.caesar.search.service.SearchHistoryService;
import org.caesar.search.manager.SearchManager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class SearchController {

    @Resource
    private SearchManager searchManager;

    @Resource
    private SearchHistoryService searchHistoryService;

    @GetMapping("/search/{dataSource}")
    public Response<PageVO<? extends IndexVO>> search(@RequestParam String text, @RequestParam int from, @RequestParam int size,
                                                      @RequestParam SortField field, @PathVariable DataSource dataSource) {
        if (Objects.isNull(field))
            return Response.ok(searchManager.search(dataSource, text, from, size));
        else
            return Response.ok(searchManager.sortSearch(dataSource, text, field, from, size));
    }

    @GetMapping("/search-batch/{dataSource}")
    Response<List<?>> searchBatch(@RequestParam List<String> texts, @RequestParam int size, @PathVariable DataSource dataSource) {
        return Response.ok(searchManager.searchBatch(texts, size, dataSource));
    }

    @GetMapping("/search-aggregation")
    Response<Map<DataSource, PageVO<? extends IndexVO>>> searchAggregation(@RequestParam String text,
                                                                           @RequestParam int from,
                                                                           @RequestParam int size) {
        return Response.ok(searchManager.searchAggregation(text, from, size));
    }

    @GetMapping("/suggestion")
    public Response<List<String>> suggestion(DataSource dataSource, String text, int size) {
        return Response.ok(searchManager.suggestion(dataSource, text, size));
    }

    @PostMapping("/sync/question-index")
    public Response<Void> syncQuestionIndex(@RequestBody List<QuestionIndexVO> indices) {
        searchManager.insertIndex(DataSource.QUESTION, indices);
        return Response.ok();
    }

    @PostMapping("/sync/article-index")
    public Response<Void> syncArticleIndex(@RequestBody List<ArticleIndexVO> indices) {
        searchManager.insertIndex(DataSource.ARTICLE, indices);
        return Response.ok();
    }

    @DeleteMapping("/sync/{dataSource}")
    public Response<Void> deleteIndex(@RequestBody List<Long> ids, @PathVariable DataSource dataSource) {
        searchManager.deleteIndex(dataSource, ids);
        return Response.ok();
    }

    @GetMapping("/history")
    public Response<List<SearchHistoryVO>> getSearchHistory(@RequestParam Integer size) {
        long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(searchHistoryService.getSearchHistory(userId, size));
    }

}
