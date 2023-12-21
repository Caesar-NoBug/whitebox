package org.caesar.common.client;

import org.caesar.domain.common.vo.PageVO;
import org.caesar.common.vo.Response;
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

    @GetMapping("/search/{dataSource}")
    <T> Response<PageVO<T>> search(@RequestParam String text, @RequestParam int from, @RequestParam int size,
                                   @RequestParam SortField field, @PathVariable DataSource dataSource);

    @GetMapping("/searchBatch")
    <T> Response<List<T>> searchBatch(@RequestParam List<String> texts, @RequestParam int size, @RequestParam DataSource dataSource);

    @GetMapping("/history")
    Response<List<SearchHistoryVO>> getSearchHistory(@RequestParam Integer size);

    @PostMapping("/sync/article-index")
    Response<Void> syncArticleIndex(@RequestBody List<ArticleIndexVO> indices);

    @PostMapping("/sync/question-index")
    Response<Void> syncQuestionIndex(@RequestBody List<QuestionIndexVO> indices);

    @DeleteMapping("/sync/{source}")
    Response<Void> deleteIndex(@RequestBody List<Long> ids, @PathVariable DataSource source);
}
