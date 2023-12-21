package org.caesar.common.client;

import org.caesar.common.vo.Response;
import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("article-service")
public interface ArticleClient {

    @GetMapping("/article/prefer")
    Response<GetPreferArticleResponse> getPreferArticle(
            @RequestParam Integer viewedSize,
            @RequestParam Integer preferredSize,
            @RequestParam Integer randPreferredSize);

    @PostMapping("/article/unique")
    Response<List<Long>> getUniqueArticle(@RequestBody List<Long> articleIds);

}
