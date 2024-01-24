package org.caesar.common;

import com.alibaba.fastjson.JSON;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.util.IOUtil;
import org.caesar.domain.constant.Headers;
import org.caesar.common.redis.RedisJsonSerializer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

@Configuration
@ComponentScan
@ComponentScan("org.caesar.common")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(RedisAutoConfiguration.class)
public class GlobalConfig {

    private final String source = "gateway";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
