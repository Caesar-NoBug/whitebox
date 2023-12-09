package org.caesar.article.constant;

public class RedisPrefix {
    // 文章自增id
    public static final String ARTICLE_INC_ID = "article:incId:";
    // 评论自增id
    public static final String COMMENT_INC_ID = "comment:incId:";
    // 文章浏览数
    public static final String ARTICLE_VIEW_COUNT = "article:viewCount:";
    // 文章点赞数
    public static final String ARTICLE_LIKE_COUNT = "article:likeCount:";
    // 文章收藏数
    public static final String ARTICLE_FAVOR_COUNT = "article:favorCount:";
    // 文章缓存
    public static final String CACHE_ARTICLE = "article:cache:";
    // 评论缓存
    public static final String CACHE_COMMENT = "comment:cache:";
    // 评论点赞数
    public static final String COMMENT_LIKE_COUNT = "article:likeCount:";

}
