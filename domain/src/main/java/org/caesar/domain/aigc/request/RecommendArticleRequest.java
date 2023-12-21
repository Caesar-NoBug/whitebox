package org.caesar.domain.aigc.request;

import lombok.Data;

@Data
public class RecommendArticleRequest {

    // 用户输入的提示
    private String userPrompt = "null";

}
