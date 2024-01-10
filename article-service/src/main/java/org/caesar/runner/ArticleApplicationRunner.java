package org.caesar.runner;

import org.caesar.common.repository.CacheRepository;
import org.caesar.repository.ArticleRepository;
import org.caesar.util.RedisKey;
import org.redisson.api.RBloomFilter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
// 项目初始化执行流程
public class ArticleApplicationRunner implements ApplicationRunner {

    @Resource
    private CacheRepository cacheRepository;

    @Resource
    private ArticleRepository articleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initBloomFilter();
    }

    // 初始化布隆过滤器
    private void initBloomFilter() {
        RBloomFilter<Long> bloomFilter = cacheRepository.getBloomFilter(RedisKey.ARTICLE_BLOOM_FILTER);


    }
}
