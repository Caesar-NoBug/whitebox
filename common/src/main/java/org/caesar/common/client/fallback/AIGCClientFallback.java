package org.caesar.common.client.fallback;
import org.caesar.common.client.AIGCClient;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AIGCClientFallback implements AIGCClient {

    @Override
    public Response<AnalyseTextResponse> analyseText(AnalyseTextRequest request) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[AIGC Service] 'analyseText' service unavailable");
    }

    @Override
    public Response<List<ArticleMinVO>> recommendArticle(String userPrompt) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[AIGC Service] 'recommendArticle' service unavailable");
    }

    @Override
    public Response<QuestionHelperResponse> questionHelper(QuestionHelperRequest request) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[AIGC Service] 'questionHelper' service unavailable");
    }

}
