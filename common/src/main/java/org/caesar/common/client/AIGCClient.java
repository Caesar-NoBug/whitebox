package org.caesar.common.client;

import org.caesar.common.vo.Response;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("aigc-service")
public interface AIGCClient {

    @PostMapping("/analyse-text")
    Response<AnalyseTextResponse> analyseText(@RequestBody AnalyseTextRequest request);

}
