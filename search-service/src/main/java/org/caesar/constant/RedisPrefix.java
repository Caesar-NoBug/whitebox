package org.caesar.constant;

public class RedisPrefix {
    // 缓存补全 参数：数据源 用户输入
    public static final String CACHE_SUGGESTION = "cache:suggestion:%s:%s";
    // 缓存检索结果 参数：数据源 用户输入
    public static final String CACHE_SEARCH_RESULT = "cache:searchResult:%s:%s";
    // 缓存检索结果 参数：数据源 排序字段 用户输入
    public static final String CACHE_SORT_SEARCH_RESULT = "cache:searchSortResult:%s:%s:%s";
}
