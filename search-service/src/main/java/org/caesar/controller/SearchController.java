package org.caesar.controller;

import org.caesar.common.Response;
import org.caesar.domain.constant.enums.DataSource;
import org.caesar.domain.constant.enums.ErrorCode;
import org.caesar.domain.constant.enums.SortField;
import org.caesar.domain.vo.search.Index;
import org.caesar.common.model.vo.Page;
import org.caesar.service.SearchService;
import org.caesar.util.SearchServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class SearchController {

    @Autowired
    private SearchServiceFactory factory;

    @GetMapping("/search/{source}")
    public Response<Page<Index>> search(@RequestParam String keyword, @RequestParam int from, @RequestParam int size,
                                        @RequestParam SortField field, @PathVariable DataSource source) {
        SearchService<Index> searchService = factory.getSearchService(source);
        Page<Index> pageResponse = Objects.isNull(field) ? searchService.search(keyword, from, size) : searchService.sortSearch(keyword, field, from, size);
        return Response.ok(pageResponse);
    }

    @PostMapping("/sync/{source}")
    public Response<Void> syncIndex(@RequestBody List<Index> indices, @PathVariable DataSource source) {
        SearchService<Index> searchService = factory.getSearchService(source);
        boolean success = searchService.insertIndex(indices, source);
        return success ? Response.ok(null) : Response.error(ErrorCode.SYSTEM_ERROR, "同步索引失败");
    }

    @DeleteMapping("/sync/{source}")
    public Response<Void> deleteIndex(@RequestBody List<Long> ids, @PathVariable DataSource source) {
        SearchService<Index> searchService = factory.getSearchService(source);
        boolean success = searchService.deleteIndex(ids, source);
        return success ? Response.ok(null) : Response.error(ErrorCode.SYSTEM_ERROR, "同步索引失败");
    }

}
