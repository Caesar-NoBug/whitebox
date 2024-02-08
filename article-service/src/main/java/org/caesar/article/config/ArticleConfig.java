package org.caesar.article.config;

import org.caesar.common.batch.cache.CacheIncTaskHandler;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.util.DataFilter;
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

    @Bean
    public CacheIncTaskHandler cacheBatchTaskHandler(CacheRepository cacheRepository) {
        long batchUpdateCacheInterval = 1000;
        return new CacheIncTaskHandler(cacheRepository, batchUpdateCacheInterval);
    }

    @Bean
    public DataFilter articleFilter(CacheRepository cacheRepository) {
        String ARTICLE_PREFIX = "article";
        return new DataFilter(cacheRepository, ARTICLE_PREFIX, maxArticleSize, articleFalseProbability);
    }

    @Bean
    public DataFilter commentFilter(CacheRepository cacheRepository) {
        String COMMENT_PREFIX = "comment";
        return new DataFilter(cacheRepository, COMMENT_PREFIX, maxCommentSize, commentFalseProbability);
    }

}
