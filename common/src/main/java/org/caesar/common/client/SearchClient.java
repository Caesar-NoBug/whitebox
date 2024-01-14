package org.caesar.common.client;

import org.caesar.common.log.Logger;
import org.caesar.domain.common.vo.PageVO;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.ArticleIndexVO;
import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.domain.search.vo.SearchHistoryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("search-service")
public interface SearchClient {

    @Logger("[RPC] /search")
    @GetMapping("/search/{dataSource}")
    <T> Response<PageVO<T>> search(@RequestParam String text, @RequestParam int from, @RequestParam int size,
                                   @RequestParam SortField field, @PathVariable DataSource dataSource);

    @Logger("[RPC] /searchBatch")
    @GetMapping("/searchBatch")
    <T> Response<List<T>> searchBatch(@RequestParam List<String> texts, @RequestParam int size, @RequestParam DataSource dataSource);

    @Logger("[RPC] /getSearchHistory")
    @GetMapping("/history")
    Response<List<SearchHistoryVO>> getSearchHistory(@RequestParam Integer size);

    @Logger("[RPC] /syncArticleIndex")
    @PostMapping("/sync/article-index")
    Response<Void> syncArticleIndex(@RequestBody List<ArticleIndexVO> indices);

    @Logger("[RPC] /syncQuestionIndex")
    @PostMapping("/sync/question-index")
    Response<Void> syncQuestionIndex(@RequestBody List<QuestionIndexVO> indices);

    @Logger("[RPC] /deleteIndex")
    @DeleteMapping("/sync/{source}")
    Response<Void> deleteIndex(@RequestBody List<Long> ids, @PathVariable DataSource source);
}
