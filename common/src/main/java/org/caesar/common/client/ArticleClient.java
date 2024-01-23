package org.caesar.common.client;

import org.caesar.common.client.fallback.ArticleClientFallback;
import org.caesar.common.log.Logger;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "article-service", fallback = ArticleClientFallback.class)
public interface ArticleClient {

    @Logger("[RPC] /getPreferArticle")
    @GetMapping("/article/prefer")
    Response<GetPreferArticleResponse> getPreferArticle(
            @RequestParam Integer viewedSize,
            @RequestParam Integer preferredSize,
            @RequestParam Integer randPreferredSize);

    @Logger("[RPC] /getUniqueArticle")
    @PostMapping("/article/unique")
    Response<List<Long>> getUniqueArticle(@RequestBody List<Long> articleIds);

}
