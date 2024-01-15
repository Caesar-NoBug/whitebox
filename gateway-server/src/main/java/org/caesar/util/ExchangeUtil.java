package org.caesar.util;


import com.alibaba.fastjson.JSON;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ExchangeUtil {

    public static<T> Mono<Void> returnError(ServerWebExchange exchange, Response<T> res) {

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Response<Object> resp = new Response<>(res.getCode(), null, res.getMsg());

        return response
                .writeWith(Mono.just(response.bufferFactory().wrap(JSON.toJSONString(resp).getBytes())))
                .then(Mono.fromRunnable(response::setComplete));
    }


}
