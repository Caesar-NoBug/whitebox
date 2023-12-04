package org.caesar.common.client;

import org.caesar.domain.article.request.AddArticleRequest;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("aigc-service")
public interface AIGCClient {

    String analyseArticle(AddArticleRequest request);

}
