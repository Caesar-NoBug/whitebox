package org.caesar.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.caesar.common.constant.Headers;
import org.caesar.common.filter.CheckSourceFilter;
import org.caesar.common.util.RedisJsonSerializer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@ComponentScan
@Import(RedisAutoConfiguration.class)
public class GlobalConfig {

    private String source = "gateway";

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RedisTemplate<Object, Object> redisJSONTemplate(RedisConnectionFactory factory){

        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        RedisJsonSerializer serializer = new RedisJsonSerializer(Object.class);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header(Headers.SOURCE_HEADER, "gateway");
                // 添加其他全局请求头
            }
        };
    }

    @Bean
    public OncePerRequestFilter oncePerRequestFilter() {
        return new CheckSourceFilter(source);
    }

}
