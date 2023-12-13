package org.caesar.controller;

import org.caesar.common.vo.Response;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.Index;
import org.caesar.common.model.vo.PageVO;
import org.caesar.service.SearchManager;
import org.caesar.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class SearchController {

    @Autowired
    private SearchManager manager;

    @GetMapping("/search/{dataSource}")
    public Response<PageVO<? extends Index>> search(@RequestParam String text, @RequestParam int from, @RequestParam int size,
                                                    @RequestParam SortField field, @PathVariable DataSource dataSource) {
        if(Objects.isNull(field))
            return Response.ok(manager.search(dataSource, text, from, size));
        else
            return Response.ok(manager.sortSearch(dataSource, text, field, from, size));
    }

    @GetMapping("/suggestion")
    public Response<List<String>> suggestion(DataSource dataSource, String text, int size) {
        return Response.ok(manager.suggestion(dataSource, text, size));
    }

    @PostMapping("/sync/{dataSource}")
    public Response<Void> syncIndex(@RequestBody List<Index> indices, @PathVariable DataSource dataSource) {
        boolean success = manager.insertIndex(dataSource, indices);
        return success ? Response.ok() : Response.error(ErrorCode.SYSTEM_ERROR, "更新索引失败");
    }

    @DeleteMapping("/sync/{dataSource}")
    public Response<Void> deleteIndex(@RequestBody List<Long> ids, @PathVariable DataSource dataSource) {
        boolean success = manager.deleteIndex(dataSource, ids);
        return success ? Response.ok() : Response.error(ErrorCode.SYSTEM_ERROR, "删除索引失败");
    }

}
