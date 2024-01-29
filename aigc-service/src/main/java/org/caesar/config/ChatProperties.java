package org.caesar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "chat")
public class ChatProperties {

    public static final String DETECT_TEXT = "detect-text";
    public static final String ANALYSE_TEXT = "analyse-text";
    public static final String RECOMMEND_USER_PROFILE = "recommend-user-profile";
    public static final String RECOMMEND_CANDIDATE_ARTICLE = "recommend-candidate-article";
    public static final String RECOMMEND_SELECT_ARTICLE = "recommend-select-article";
    public static final String QUESTION_HELPER = "question-helper";

    private Map<String, ChatConfig> config;

    public ChatConfig getChatConfig(String key) {
        return config.get(key);
    }

}
