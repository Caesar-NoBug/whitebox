package org.caesar.service;

import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.CompletionResponse;
import org.caesar.domain.aigc.response.QuestionHelperResponse;

public interface ChatService {
    //  对话
    CompletionResponse completion(CompletionRequest request);

    //  问题解决
    QuestionHelperResponse questionHelper(QuestionHelperRequest request);

    //  助手
    CompletionResponse assistant(CompletionRequest request);

    //  总结内容
    CompletionResponse summary(CompletionRequest request);

}
