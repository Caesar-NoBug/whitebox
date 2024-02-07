package org.caesar.article.config;

import org.caesar.common.batch.CacheIncBatchTaskHandler;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.util.DataFilter;
import org.caesar.article.constant.CacheKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleConfig {

    // 预计的最大文章数量
    @Value("${data-filter.article.size}")
    private Integer maxArticleSize;

    // 预计的最大文章数量
    @Value("${data-filter.article.falseProbability}")
    private Double articleFalseProbability;

    // 预计的最大文章数量
    @Value("${data-filter.comment.size}")
    private Integer maxCommentSize;

    // 预计的最大文章数量
    @Value("${data-filter.comment.falseProbability}")
    private Double commentFalseProbability;

    private final long batchUpdateCacheInterval = 1000;

    @Bean
    public CacheIncBatchTaskHandler cacheBatchTaskHandler(CacheRepository cacheRepository) {
        return new CacheIncBatchTaskHandler(cacheRepository, batchUpdateCacheInterval);
    }

    @Bean
    public DataFilter articleFilter(CacheRepository cacheRepository) {
        return new DataFilter(cacheRepository, CacheKey.ARTICLE_BLOOM_FILTER,
                CacheKey.ARTICLE_REMOVED_BITSET, maxArticleSize, articleFalseProbability);
    }

    @Bean
    public DataFilter commentFilter(CacheRepository cacheRepository) {
        return new DataFilter(cacheRepository, CacheKey.COMMENT_BLOOM_FILTER,
                CacheKey.COMMENT_REMOVED_BITSET, maxCommentSize, commentFalseProbability);
    }

}
