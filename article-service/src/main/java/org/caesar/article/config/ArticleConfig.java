package org.caesar.article.config;

import org.caesar.common.cache.CacheRepository;
import org.caesar.common.util.DataFilter;
import org.caesar.article.constant.CacheKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleConfig {

    // 预计的最大文章数量
    @Value("${article.bloomFilter.size}")
    private Integer size;

    // 预计的最大文章数量
    @Value("${article.bloomFilter.falseProbability}")
    private Double falseProbability;

    @Bean
    public DataFilter articleFilter(CacheRepository cacheRepository) {
        return new DataFilter(cacheRepository, CacheKey.ARTICLE_BLOOM_FILTER,
                CacheKey.ARTICLE_REMOVED_BITSET, size, falseProbability);
    }

}
