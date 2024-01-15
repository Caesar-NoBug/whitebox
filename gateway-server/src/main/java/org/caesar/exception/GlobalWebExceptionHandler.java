package org.caesar.exception;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.common.exception.BusinessException;
import org.caesar.util.ExchangeUtil;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//TODO: 统一异常处理
@ControllerAdvice
@Slf4j
public class GlobalWebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        String msg = ex.getMessage();
        ErrorCode code;
        //TODO: 详细打印错误日志
        //TODO: 把日志同步到ES中，方便查看
        log.error(msg);
        if(ex instanceof BusinessException) {
            code = ((BusinessException) ex).getCode();
        }
        else {
            code = ErrorCode.SYSTEM_ERROR;
        }

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Response<Object> resp = new Response<>(code.getCode(), null, msg);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(JSON.toJSONString(resp).getBytes())));
    }

}
