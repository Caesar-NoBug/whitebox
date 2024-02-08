package org.caesar.common.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

@Configuration
public class FeignConfig {

    @Bean
    Encoder feignEncoder() {
        return (o, type, requestTemplate) -> requestTemplate.body(JSON.toJSONString(o));
    }

    @Bean
    Decoder feignDecoder() {
        return (response, type) -> {
            String jsonString = IOUtil.readAll(response.body().asInputStream());
            System.out.println(jsonString);
            return JSON.parseObject(jsonString, type);
        };
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {

            System.out.println("feign thread:" + Thread.currentThread().getId());
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
}
