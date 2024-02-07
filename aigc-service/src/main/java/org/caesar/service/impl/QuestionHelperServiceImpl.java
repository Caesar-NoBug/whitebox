package org.caesar.service.impl;

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


    @Override
    public QuestionHelperResponse questionHelper(QuestionHelperRequest request) {

        String description = request.getDescription();
        String code = request.getCode();
        String message = request.getMessage();
        String result = request.getResult();
        String answer = request.getAnswer();

        return null;
    }
}
