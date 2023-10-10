package org.caesar.filter;

import com.alibaba.fastjson.JSON;
import org.caesar.common.model.dto.request.question.SubmitCodeRequest;
import org.caesar.publisher.ExecuteMessagePublisher;
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

        //TODO: 处理提交的逻辑移动到RequestRedirectFilter中
        if(SUBMIT_URI.equals(uri)) {

            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        String body = new String(bytes, StandardCharsets.UTF_8);
                        SubmitCodeRequest request = JSON.parseObject(body, SubmitCodeRequest.class);
                        producer.sendExecuteMessage(request);
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
