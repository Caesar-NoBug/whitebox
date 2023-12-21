package org.caesar.service.impl;

import org.caesar.common.exception.BusinessException;
import org.caesar.constant.ChatPrompt;
import org.caesar.constant.Patterns;
import org.caesar.domain.aigc.request.AnalyseTextRequest;
import org.caesar.domain.aigc.request.CompletionRequest;
import org.caesar.domain.aigc.response.AnalyseTextResponse;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.service.AnalyseService;
import org.caesar.service.ChatService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.regex.Matcher;

@Service
public class AnalyseServiceImpl implements AnalyseService {

    @Resource
    private ChatService chatService;

    @Override
    public AnalyseTextResponse analyseText(AnalyseTextRequest request) {
        String title = request.getTitle();
        String content = simplifyArticle(request.getContent());
        boolean genContent = request.isGenContent();
        String prompt = String.format(ChatPrompt.PROMPT_TEXT_INFO, title, content);

        // 审核文章
        String detectResult = chatService.completion(new CompletionRequest(ChatPrompt.PRESET_DETECT_TEXT, prompt)).getReply();

        AnalyseTextResponse response = new AnalyseTextResponse();

        // 是否通过审核
        response.setPass(ChatPrompt.DETECT_TEXT_PASS.equals(detectResult));

        // 未通过或不生成摘要、标签则直接结束
        if(!response.isPass() || !genContent)
            return response;

        String analyseResult = chatService.completion(new CompletionRequest(ChatPrompt.PRESET_ANALYSE_ARTICLE, prompt)).getReply();

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
}
