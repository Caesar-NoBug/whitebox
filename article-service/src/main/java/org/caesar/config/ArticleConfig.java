package org.caesar.config;

import org.caesar.common.repository.CacheRepository;
import org.caesar.common.repository.RedisCacheRepository;
import org.caesar.util.DataFilter;
import org.caesar.util.RedisKey;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class ArticleConfig {

    // 预计的最大文章数量
    @Value("${article.bloomFilter.size}")
    private Integer size;

    // 预计的最大文章数量
    @Value("${article.bloomFilter.falseProbability}")
    private Double falseProbability;

    @Bean
    public DataFilter articleDataFilter(CacheRepository cacheRepository) {
        return new DataFilter(cacheRepository, RedisKey.ARTICLE_BLOOM_FILTER,
                RedisKey.ARTICLE_REMOVED_BITSET, size, falseProbability);
    }

}
