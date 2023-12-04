package org.caesar.service.impl;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.caesar.client.ChatClient;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.constant.ChatPrompt;
import org.caesar.constant.RedisKey;

import org.caesar.domain.aigc.request.AnalyseArticleRequest;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.AnalyseArticleResponse;
import org.caesar.domain.aigc.response.QuestionHelperResponse;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.model.vo.*;
import org.caesar.service.ChatService;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.repository.CacheRepository;
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

        String userId = ContextHolder.get(ContextHolder.USER_ID);
        ThrowUtil.ifNull(userId, "对话失败：用户未登录");

        //  继续对话
        if (id != null) {

            //  从缓存中获取对话信息
            cacheKey = String.format(RedisKey.CHAT_COMPLETION, userId, id);
            log.info("聊天请求：用户id：" + userId + "，对话id：" + id);
            completion = cacheRepo.getObject(cacheKey);

            ThrowUtil.ifNull(completion, "非法对话id，对话信息不存在");

            response = continueCompletion(completionRequest, completion);
        }
        //  开始新的对话
        else {

            id = UUID.fastUUID().toString();
            completionRequest.setId(id);
            cacheKey = String.format(RedisKey.CHAT_COMPLETION, userId, id);
            log.info("聊天请求：用户id：" + userId + "，对话id：" + id);
            completion = new OpenAIChatCompletion(completionRequest);

            response = startCompletion(completionRequest, completion);
        }

        //  保存对话信息
        if(completionRequest.getMemory() > 0)
            cacheRepo.setObject(cacheKey, completion);

        return response;
    }

    @Override
    public QuestionHelperResponse questionHelper(QuestionHelperRequest request) {

        return null;
    }

    @Override
    public CompletionResponse assistant(CompletionRequest request) {
        return null;
    }

    @Override
    public CompletionResponse summary(CompletionRequest request) {
        return null;
    }

    @Override
    public AnalyseArticleResponse analyseArticle(AnalyseArticleRequest request) {
        String title = request.getTitle();
        String content = request.getContent();
        boolean genContent = request.isGenContent();
        String prompt = String.format(ChatPrompt.PROMPT_ARTICLE_INFO, title, content);

        String detectResult = completion(new CompletionRequest(ChatPrompt.PRESET_DETECT_ARTICLE, prompt)).getReply();

        AnalyseArticleResponse response = new AnalyseArticleResponse();
        // 是否通过审核
        response.setPass(ChatPrompt.DETECT_ARTICLE_FAIL.equals(detectResult));

        // 未通过或不生成摘要、标签则直接结束
        if(!response.isPass() || !genContent)
            return response;

        String analyseResult = completion(new CompletionRequest(ChatPrompt.PRESET_ANALYSE_ARTICLE, prompt)).getReply();

        String[] generatedContents = analyseResult.split("\n");

        try {
            response.setDigest(generatedContents[0]);
            response.setDigest(generatedContents[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
             throw new BusinessException(ErrorCode.SYSTEM_ERROR, "ai响应结果格式错误");
        }

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
