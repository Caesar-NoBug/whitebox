package org.caesar.service;

import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.QuestionHelperResponse;

public interface QuestionHelperService {
    QuestionHelperResponse questionHelper(QuestionHelperRequest request);
}
