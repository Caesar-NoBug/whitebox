package org.caesar.service.impl;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.caesar.client.ChatClient;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.constant.ChatPrompt;
import org.caesar.constant.Patterns;
import org.caesar.constant.RedisKey;

import org.caesar.domain.aigc.request.AnalyseContentRequest;
import org.caesar.domain.aigc.request.QuestionHelperRequest;
import org.caesar.domain.aigc.response.AnalyseContentResponse;
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
import java.util.regex.Matcher;

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
        ThrowUtil.ifNull(userId, "对话失败：用户未登录");
        //TODO: 加一个向user-service的异步计费请求
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
    public AnalyseContentResponse analyseContent(AnalyseContentRequest request) {
        String title = request.getTitle();
        String content = simplifyArticle(request.getContent());
        boolean genContent = request.isGenContent();
        String prompt = String.format(ChatPrompt.PROMPT_ARTICLE_INFO, title, content);

        // 审核文章
        String detectResult = completion(new CompletionRequest(ChatPrompt.PRESET_DETECT_ARTICLE, prompt)).getReply();

        AnalyseContentResponse response = new AnalyseContentResponse();

        // 是否通过审核
        response.setPass(ChatPrompt.DETECT_ARTICLE_PASS.equals(detectResult));

        // 未通过或不生成摘要、标签则直接结束
        if(!response.isPass() || !genContent)
            return response;

        String analyseResult = completion(new CompletionRequest(ChatPrompt.PRESET_ANALYSE_ARTICLE, prompt)).getReply();

        String[] generatedContents = analyseResult.split("\n\n");

        try {
            response.setDigest(generatedContents[0]);
            response.setTags(generatedContents[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
             throw new BusinessException(ErrorCode.SYSTEM_ERROR, "分析响应结果格式错误");
        }

        return response;
    }

    /**
     * 去除文章中的代码块（但保留代码块中的注释）
     * @param content 文章内容（假设为md形式）
     * @return 简化后的文章内容
     */
    private String simplifyArticle(String content) {

        Matcher codeBlockMatcher = Patterns.CODE_BLOCK_PATTERN.matcher(content);

        StringBuffer replacedText = new StringBuffer();

        // 遍历匹配的代码块
        while (codeBlockMatcher.find()) {
            String codeBlock = codeBlockMatcher.group();

            // 保留代码中的注释
            Matcher commentMatcher = Patterns.COMMENT_PATTERN.matcher(codeBlock);
            StringBuilder sb = new StringBuilder();
            while (commentMatcher.find()) {
                sb.append(commentMatcher.group()).append('\n');
            }
            codeBlockMatcher.appendReplacement(replacedText, Matcher.quoteReplacement(sb.toString()));
        }

        codeBlockMatcher.appendTail(replacedText);

        return replacedText.toString();
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
