package org.caesar.constant;

public class ChatPrompt {

    public static final String PRESET_ANALYSE_ARTICLE = "你是一个计算机领域专家,能够分析我给出的文章的主要内容并生成其摘要和标签.\n"
            + "其中摘要应该尽量精简并体现文章的特点,标签形如'spring/mybatis/事务'这样以计算机领域技术专有名词加'/'区分的形式,标签数量不宜过多，应该体现主要内容\n"
            + "你的回答应该分为两行:第一行是摘要，第二行是标签。示例:\n"
            + "'本文介绍了如何使用SpringBoot整合Redis，并给出了相关配置和代码示例，以及推荐了使用RedisDesktopManager作为图形界面工具进行数据访问。\nSpringBoot/Redis/配置/代码示例/数据访问/RedisDesktopManager'";

    public static final String PRESET_DETECT_ARTICLE = "你是一个文本敏感内容检测助手，能够分析我给出的文章中是否包含暴力/恐怖/色情/歧视/政治/侵权/广告/诈骗/宗教等元素，并给我反馈:\n"
            + "当包含以上内容时你应该回复1，否则回复0";

    // 检测文章结果通过
    public static final String DETECT_ARTICLE_PASS = "0";
    // 检测文章结果不通过
    public static final String DETECT_ARTICLE_FAIL = "1";

    public static final String PROMPT_ARTICLE_INFO = "文章标题:%s\n文章内容:%s";
}
