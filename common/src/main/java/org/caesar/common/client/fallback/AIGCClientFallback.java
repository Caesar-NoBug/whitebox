package org.caesar.common.client.fallback;


import feign.hystrix.FallbackFactory;
import org.caesar.common.client.AIGCClient;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.springframework.stereotype.Component;

@Component
public class AIGCClientFallback implements AIGCClient {

    @Override
    public Response<AnalyseTextResponse> analyseText(AnalyseTextRequest request) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[AIGC Service] 'analyseText' service unavailable");
    }

}
