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
    Encoder feignEncoder() {
        return new Encoder() {
            @Override
            public void encode(Object o, Type type, RequestTemplate requestTemplate) throws EncodeException {
                requestTemplate.body(JSON.toJSONString(o));
            }
        };
    }

    @Bean
    Decoder feignDecoder() {
        return new Decoder() {
            @Override
            public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
                String jsonString = IOUtil.readAll(response.body().asInputStream());
                System.out.println(jsonString);
                return JSON.parseObject(jsonString, type);
            }
        };
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            //TODO: 设置允许通信的服务的ip白名单，只接收来自白名单的请求，并使用https进行服务间通信
            template.header("Content-Type", "application/json");
            template.header(Headers.SOURCE_HEADER, "gateway");

            Long userId = ContextHolder.getUserId();
            if(Objects.nonNull(userId))
                template.header(Headers.USERID_HEADER, String.valueOf(userId));

            String traceId = ContextHolder.getTraceId();
            if(Objects.nonNull(traceId))
                template.header(Headers.TRACE_ID_HEADER, traceId);
            // 添加其他全局请求头
        };

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
