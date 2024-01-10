package org.caesar.constant;

import org.caesar.domain.article.vo.ArticleMinVO;

import java.util.List;

public class ChatPrompt {

    public static final String PRESET_ANALYSE_ARTICLE = "你是一个计算机领域专家,能够分析我给出的文章的主要内容并生成其摘要和标签.\n"
            + "其中摘要应该尽量精简并体现文章的特点,标签形如'spring/mybatis/事务'这样以计算机领域技术专有名词加'/'区分的形式,标签数量不宜过多，应该体现主要内容\n"
            + "你的回答应该分为两行:第一行是摘要,第二行是标签,并以'@@@'分隔。示例:\n"
            + "'本文介绍了如何使用SpringBoot整合Redis，并给出了相关配置和代码示例，以及推荐了使用RedisDesktopManager作为图形界面工具进行数据访问。@@@SpringBoot2/Redis/配置/代码示例/数据访问/RedisDesktopManager'";

    public static final String PRESET_DETECT_TEXT = "你是一个文本敏感内容检测助手，能够分析我给出的文本中是否包含暴力/恐怖/色情/歧视/政治/侵权/广告/诈骗/宗教等元素，并给我反馈:\n"
            + "当包含以上内容时你应该回复1，否则回复0,请记住你的回答只能是0或1，不要包含其他内容";

    // 检测文章结果通过
    public static final String DETECT_TEXT_PASS = "0";
    // 检测文章结果不通过
    public static final String DETECT_TEXT_FAIL = "1";

    public static final String PROMPT_TEXT_INFO = "文章标题:%s\n文章内容:%s";

    public static final String PRESET_CREATE_USER_PROFILE = "你是一个用户画像分析助手，能够根据我给你提供的用户职业/偏好/偏好的文章来分析用户的技术水平、行业背景、用户喜欢的文章有哪些特点。你的回答示例:'技术水平:高级水平:在特定技术领域有深入研究和实践经验，并能够解决复杂的技术问题。\n" +
            "行业背景:电子商务：在电子商务行业从事网站开发和电子支付系统的设计与实施。\n" +
            "偏好文章:用户喜欢阅读与后端开发相关的文章，特别是与springboot和elasticsearch相关的文章。用户对于缓存优化、虚拟机连接、ES索引的生成和ES复杂搜索等主题很感兴趣。用户可能对于提升后端性能、优化开发环境、掌握主流的后端技术有较高的关注度。'";

    public static String createUserProfilePrompt(String occupation, String preference, List<ArticleMinVO> articles, List<String> searchHistories) {
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

    public static String createCandidateArticle(String userProfile, String userPrompt) {
        return String.format("用户画像:%s\n用户提示:%s", userProfile, userPrompt);
    }

    public static final String PRESET_CREATE_CANDIDATE_ARTICLE = "你是一个文章推荐助手，能够用户画像以及用户的提示推测用户可能喜欢的文章的标题，请为我推荐三篇文章。" +
            "你的回答应该只包含3个文章标题，并以换行符分隔，除此之外不要包含任何信息，示例：深入浅出线性回归算法\n从头开始构建一个代码评测系统\nWeb开发中的后端与前端通信方式";

    public static String recommendArticlePrompt(String userProfile, String userPrompt, List<ArticleMinVO> candidates) {
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

    public static final String PRESET_RECOMMEND_ARTICLE = "你是一个文章推荐助手，能够根据我提供的用户画像在候选文章中选出用户最可能喜欢的文章，你的回答只需要包含这些文章的编号,以','分隔，示例:3,9,12";

}
