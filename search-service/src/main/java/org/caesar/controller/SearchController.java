package org.caesar.controller;

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
import org.caesar.service.SearchHistoryService;
import org.caesar.service.SearchManager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
public class SearchController {

    @Resource
    private SearchManager searchManager;

    @Resource
    private SearchHistoryService searchHistoryService;

    //TODO: 加个聚合搜索接口
    //TODO: 搜索历史接口

    @GetMapping("/search/{dataSource}")
    public Response<PageVO<? extends IndexVO>> search(@RequestParam String text, @RequestParam int from, @RequestParam int size,
                                                    @RequestParam SortField field, @PathVariable DataSource dataSource) {
        if(Objects.isNull(field))
            return Response.ok(searchManager.search(dataSource, text, from, size));
        else
            return Response.ok(searchManager.sortSearch(dataSource, text, field, from, size));
    }

    @GetMapping("/suggestion")
    public Response<List<String>> suggestion(DataSource dataSource, String text, int size) {
        return Response.ok(searchManager.suggestion(dataSource, text, size));
    }

    @PostMapping("/sync/question-index")
    public Response<Void> syncQuestionIndex(@RequestBody List<QuestionIndexVO> indices) {
        boolean success = searchManager.insertIndex(DataSource.QUESTION, indices);
        return success ? Response.ok() : Response.error(ErrorCode.SYSTEM_ERROR, "更新索引失败");
    }

    @PostMapping("/sync/article-index")
    public Response<Void> syncArticleIndex(@RequestBody List<ArticleIndexVO> indices) {
        boolean success = searchManager.insertIndex(DataSource.ARTICLE, indices);
        return success ? Response.ok() : Response.error(ErrorCode.SYSTEM_ERROR, "更新索引失败");
    }

    @DeleteMapping("/sync/{dataSource}")
    public Response<Void> deleteIndex(@RequestBody List<Long> ids, @PathVariable DataSource dataSource) {
        boolean success = searchManager.deleteIndex(dataSource, ids);
        return success ? Response.ok() : Response.error(ErrorCode.SYSTEM_ERROR, "删除索引失败");
    }

    @GetMapping("/history")
    public Response<List<SearchHistoryVO>> getSearchHistory(@RequestParam Integer size) {
        long userId = ContextHolder.getUserId();
        return Response.ok(searchHistoryService.getSearchHistory(userId, size));
    }

}
