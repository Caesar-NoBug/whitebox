package org.caesar.common.client;

import org.caesar.common.vo.Response;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.vo.Index;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("search-service")
public interface SearchServiceClient {

    @PostMapping("/sync/{source}")
    Response<Void> syncIndex(@RequestBody List<? extends Index> indices, @PathVariable DataSource source);

    @DeleteMapping("/sync/{source}")
    Response<Void> deleteIndex(@RequestBody List<Long> ids, @PathVariable DataSource source);
}
