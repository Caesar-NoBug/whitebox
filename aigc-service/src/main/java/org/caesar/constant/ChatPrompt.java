package org.caesar.constant;

import org.caesar.domain.article.vo.ArticleMinVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatPrompt {

    // 分析文章并生成标签和摘要
    @Value("${chat.analyse-text.preset}")
    public String ANALYSE_TEXT_PRESET;

    @Value("${chat.analyse-text.prompt}")
    public String ANALYSE_TEXT_PROMPT;

    @Value("${chat.analyse-text.separator}")
    public String ANALYSE_TEXT_SEPARATOR;

    @Value("${chat.detect-text.preset}")
    public String DETECT_TEXT_PRESET;

    // 检测文章结果通过
    @Value("${chat.detect-text.reply}")
    public String DETECT_TEXT_PASS;

    @Value("${chat.detect-text.prompt}")
    public String DETECT_TEXT_PROMPT;

    // 生成用户画像
    @Value("${chat.recommend-user-profile.preset}")
    public String USER_PROFILE_PRESET;

    @Value("${chat.recommend-user-profile.prompt}")
    public String USER_PROFILE_PROMPT;

    // 生成候选文章
    @Value("${chat.recommend-candidate-article.preset}")
    public String CANDIDATE_ARTICLE_PRESET;

    @Value("${chat.recommend-candidate-article.separator}")
    public String CANDIDATE_ARTICLE_SEPARATOR;

    // 从候选文章中选出推荐文章
    @Value("${chat.recommend-select-article.preset}")
    public String RECOMMEND_ARTICLE_PRESET;

    @Value("${chat.question-helper.preset}")
    public String PRESET_QUESTION_HELPER;

    public String createUserProfilePrompt(String occupation, String preference, List<ArticleMinVO> articles, List<String> searchHistories) {

        StringBuilder sb = new StringBuilder();
        sb.append("用户职业:").append(occupation).append("\n");
        sb.append("用户偏好:").append(preference).append("\n");
        sb.append("用户近期喜欢的文章:").append("\n[");

        articles.forEach(article -> {
            sb.append("{\n标题: ").append(article.getTitle())
                    .append("\n标签: ").append(article.getTag())
                    .append("\n摘要: ").append(article.getDigest())
                    .append("}");
        });

        sb.append("]");

        sb.append("用户搜索过的内容:\n");

        searchHistories.forEach(searchHistory -> {
            sb.append(searchHistory).append("\n");
        });

        return sb.toString();
    }

    public String createCandidateArticle(String userProfile, String userPrompt) {
        return String.format("用户画像:%s\n用户提示:%s", userProfile, userPrompt);
    }

    public String recommendArticlePrompt(String userProfile, String userPrompt, List<ArticleMinVO> candidates) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户画像:").append(userProfile);
        sb.append("用户提示:").append(userPrompt);
        sb.append("候选文章:\n[");

        candidates.forEach(article -> {
            sb.append("{\n编号: ").append(article.getId())
                    .append("\n标题: ").append(article.getTitle())
                    .append("\n标签: ").append(article.getTag())
                    .append("\n摘要: ").append(article.getDigest())
                    .append("}");
        });

        sb.append("]");
        return sb.toString();
    }

}
