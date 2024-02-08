package org.caesar.service.impl;

import org.caesar.config.ChatConfig;
import org.caesar.config.ChatProperties;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.service.ChatService;
import org.caesar.service.QuestionHelperService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class QuestionHelperServiceImpl implements QuestionHelperService {

    @Resource
    private ChatService chatService;

    private final ChatConfig questionHelperConfig;

    QuestionHelperServiceImpl(ChatProperties chatProperties) {
        questionHelperConfig = chatProperties.getChatConfig(ChatProperties.QUESTION_HELPER);
    }

    @Override
    public QuestionHelperResponse questionHelper(QuestionHelperRequest request) {

        String description = request.getDescription();
        String code = request.getCode();
        String message = request.getMessage();
        String result = request.getResult();
        String correctCode = request.getCorrectCode();

        String prompt = String.format(questionHelperConfig.getPrompt(), description, code, result, message, correctCode);

        String reply = chatService.completion(new CompletionRequest(questionHelperConfig.getPreset(), prompt)).getReply();

        return new QuestionHelperResponse(reply);
    }

}
