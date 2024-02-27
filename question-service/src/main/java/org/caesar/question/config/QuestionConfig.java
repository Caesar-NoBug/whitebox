package org.caesar.question.config;

import org.caesar.common.batch.cache.CacheIncTaskHandler;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.util.DataFilter;
import org.caesar.common.util.SwaggerCore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class QuestionConfig {

    // 预计的最大问题数量
    @Value("${data-filter.question.size}")
    private Integer maxArticleSize;

    // 预计的最大问题数量
    @Value("${data-filter.question.falseProbability}")
    private Double articleFalseProbability;

    @Bean
    public CacheIncTaskHandler cacheBatchTaskHandler(CacheRepository cacheRepository) {
        long batchUpdateCacheInterval = 1000;
        return new CacheIncTaskHandler(cacheRepository, batchUpdateCacheInterval);
    }

    @Bean
    public DataFilter<Long> questionFilter(CacheRepository cacheRepository) {
        final String QUESTION_PREFIX = "question";
        return new DataFilter<>(cacheRepository, QUESTION_PREFIX, maxArticleSize, articleFalseProbability);
    }

    @Bean
    Docket systemIndexApi(){
        return SwaggerCore.defaultDocketBuilder("接口领域模型定义","org.caesar","default");
    }

}
