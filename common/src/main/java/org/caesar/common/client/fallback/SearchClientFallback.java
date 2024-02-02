package org.caesar.common.client.fallback;

import org.caesar.common.client.SearchClient;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.PageVO;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.ArticleIndexVO;
import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.domain.search.vo.SearchHistoryVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchClientFallback implements SearchClient {

    @Override
    public <T> Response<PageVO<T>> search(String text, int from, int size, SortField field, DataSource dataSource) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Search Service] 'search' service unavailable");
    }

    @Override
    public <T> Response<List<T>> searchBatch(List<String> texts, int size, DataSource dataSource) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Search Service] 'searchBatch' service unavailable");
    }

    @Override
    public Response<List<SearchHistoryVO>> getSearchHistory(Integer size, DataSource dataSource) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Search Service] 'getSearchHistory' service unavailable");
    }

    @Override
    public Response<Void> syncArticleIndex(List<ArticleIndexVO> indices) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Search Service] 'syncArticleIndex' service unavailable");
    }

    @Override
    public Response<Void> syncQuestionIndex(List<QuestionIndexVO> indices) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Search Service] 'syncQuestionIndex' service unavailable");
    }

    @Override
    public Response<Void> deleteIndex(List<Long> ids, DataSource source) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[Search Service] 'deleteIndex' service unavailable");
    }
}
