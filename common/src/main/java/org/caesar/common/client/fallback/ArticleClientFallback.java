package org.caesar.common.client.fallback;

import org.caesar.common.client.ArticleClient;
import org.caesar.domain.article.response.GetPreferArticleResponse;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleClientFallback implements ArticleClient {

    @Override
    public Response<GetPreferArticleResponse> getPreferArticle(Integer viewedSize, Integer preferredSize, Integer randPreferredSize) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Article Service] 'getPreferArticle' service unavailable");
    }

    @Override
    public Response<List<Long>> getUniqueArticle(List<Long> articleIds) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Article Service] 'getUniqueArticle' service unavailable");
    }
}
