package org.caesar.gateway.filter;

import com.alibaba.fastjson.JSON;
import org.caesar.domain.constant.Headers;
import org.caesar.domain.question.request.JudgeCodeRequest;
import org.caesar.gateway.publisher.ExecuteMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class RequestHandlerFilter implements GlobalFilter, Ordered {

    public static final String SUBMIT_URI = "/question-service/submit";

    @Autowired
    private ExecuteMessagePublisher producer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String uri = exchange.getRequest().getURI().getPath();

        if(SUBMIT_URI.equals(uri)) {

            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {

                        //解析请求
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        String body = new String(bytes, StandardCharsets.UTF_8);
                        String userId = exchange.getRequest().getHeaders().getFirst(Headers.USERID_HEADER);
                        JudgeCodeRequest request = JSON.parseObject(body, JudgeCodeRequest.class);
                        request.setUserId(Long.parseLong(userId));

                        //发送消息
                        producer.sendJudgeMessage(request);
                        //TODO: 设置响应
                        return chain.filter(exchange);
                    });
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -5;
    }

}