package org.caesar.article.constant;

public class CacheKey {

    public static final String ARTICLE_BLOOM_FILTER = "article:bloomFilter";

    public static final String ARTICLE_REMOVED_BITSET = "article:removed:bitset";
    // 文章自增id
    private static final String ARTICLE_INC_ID = "article:incId:";
    // 评论自增id
    private static final String COMMENT_INC_ID = "comment:incId:";
    // 文章浏览数
    private static final String ARTICLE_VIEW_COUNT = "article:viewCount:";
    // 文章点赞数
    private static final String ARTICLE_LIKE_COUNT = "article:likeCount:";
    // 文章收藏数
    private static final String ARTICLE_FAVOR_COUNT = "article:favorCount:";
    // 文章缓存
    private static final String CACHE_ARTICLE = "article:cache:";
    // 评论缓存
    private static final String CACHE_COMMENT = "comment:cache:";
    // 评论点赞数
    private static final String COMMENT_LIKE_COUNT = "article:likeCount:";
    // 全部文章近期浏览记录集合
    private static final String CANDIDATE_ARTICLE_SET = "article:candidate:set";
    // 热门文章集合
    private static final String HOT_ARTICLE_SET = "article:hot:set";
    // 文章浏览历史记录
    private static final String ARTICLE_HISTORY_ZSET = "article:hot:zset:";

    public static String articleIncId() {
        return ARTICLE_INC_ID;
    }

    public static String commentIncId() {
        return COMMENT_INC_ID;
    }
    public static String hotArticleSet() {
        return HOT_ARTICLE_SET;
    }
    public static String candidateArticleSet() {
        return CANDIDATE_ARTICLE_SET;
    }
    public static String articleHistorySet(long articleId) {
        return ARTICLE_HISTORY_ZSET + articleId;
    }
    public static String articleViewCount(long articleId) {
        return ARTICLE_VIEW_COUNT + articleId;
    }
    public static String articleLikeCount(long articleId) {
        return ARTICLE_LIKE_COUNT + articleId;
    }
    public static String articleFavorCount(long articleId) {
        return ARTICLE_FAVOR_COUNT + articleId;
    }

    public static String cacheArticle(long articleId) {
        return CACHE_ARTICLE + articleId;
    }

    public static String commentLikeCount(long commentId) {
        return COMMENT_LIKE_COUNT + commentId;
    }

    public static String cacheComment(long commentId) {
        return CACHE_COMMENT + commentId;
    }
}
