package org.caesar.service.impl;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.caesar.client.ChatClient;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.log.LogUtil;
import org.caesar.constant.RedisKey;

import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.model.entity.OpenAIChatCompletion;
import org.caesar.model.vo.*;
import org.caesar.service.ChatService;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.cache.CacheRepository;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.response.CompletionResponse;
import org.caesar.model.vo.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class OpenAIChatService implements ChatService {

    @Resource
    private ChatClient chatClient;

    @Resource
    private CacheRepository cacheRepo;

    @Override
    public CompletionResponse completion(CompletionRequest completionRequest) {

        String id = completionRequest.getId();
        OpenAIChatCompletion completion;
        CompletionResponse response;
        String cacheKey;

        Long userId = ContextHolder.get(ContextHolder.USER_ID);
        ThrowUtil.ifNull(userId, "Fail to start a completion: unauthenticated.");
        //TODO: 加一个向user-service的计费请求

        //  继续对话
        if (id != null) {

            //  从缓存中获取对话信息
            cacheKey = String.format(RedisKey.CHAT_COMPLETION, userId, id);
            LogUtil.bizLog("Continue chat completion, completion id " + id + ".");
            completion = cacheRepo.getObject(cacheKey);
            ThrowUtil.ifNull(completion, ErrorCode.NOT_FIND_ERROR, "Invalid completion id: chat completion does not exists or expired");

            response = continueCompletion(completionRequest, completion);
        }
        //  开始新的对话
        else {

            id = UUID.fastUUID().toString();
            completionRequest.setId(id);
            cacheKey = String.format(RedisKey.CHAT_COMPLETION, userId, id);
            LogUtil.bizLog("Start chat completion, completion id " + id + ".");
            completion = new OpenAIChatCompletion(completionRequest);

            response = startCompletion(completionRequest, completion);
        }

        //  保存对话信息
        if(completionRequest.getMemory() > 0)
            cacheRepo.setObject(cacheKey, completion);

        return response;
    }

    //  开始新对话
    private CompletionResponse startCompletion(CompletionRequest completionRequest, OpenAIChatCompletion completion) {
        return handleCompletion(completionRequest.getId(), completion);
    }

    //  继续对话
    private CompletionResponse continueCompletion(CompletionRequest completionRequest, OpenAIChatCompletion completion) {

        //  获取模型预设及历史记录
        List<Message> messages = completion.getMessages();

        //  添加用户新消息
        messages.add(Message.prompt(completionRequest.getPrompt()));

        //  执行对话过程
        return handleCompletion(completionRequest.getId(), completion);
    }

    private CompletionResponse handleCompletion(String id, OpenAIChatCompletion completion) {

        //  构造请求
        OpenAIChatRequest request = new OpenAIChatRequest(completion);

        //  发起对话
        OpenAIChatResponse chatResponse = (OpenAIChatResponse) chatClient.chat(request);

        //  更新对话信息
        completion.update(chatResponse);

        //  设置响应内容
        CompletionResponse response = new CompletionResponse();
        Message reply = chatResponse.getChoices().get(0).getMessage();

        response.setId(id);
        response.setReply(reply.getContent());

        return response;
    }

}
