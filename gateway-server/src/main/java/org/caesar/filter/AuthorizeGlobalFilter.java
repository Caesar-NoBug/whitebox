package org.caesar.filter;

import org.caesar.client.UserClient;
import org.caesar.common.Response;
import org.caesar.domain.constant.Headers;
import org.caesar.domain.constant.enums.ErrorCode;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.util.PrefixMatcher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

//TODO: 统一的异常处理机制
//对请求进行认证和鉴权，并在请求上加上用户id
@Component
public class AuthorizeGlobalFilter implements GlobalFilter, Ordered {

    public static final String[] AUTHORIZE_WHITE_LIST = {"/user-service/user/login/**", "/user-service/user/sendCode/**",
            "/user-service/user/refreshToken", "/user-service/user/register/**", "/user-service/user/reset"};

    public static final PrefixMatcher prefixMatcher = new PrefixMatcher(AUTHORIZE_WHITE_LIST);

    @Autowired
    private ObjectProvider<UserClient> userClientProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("authorize filter");
        String uri = exchange.getRequest().getURI().getPath();

        //若在白名单中，则无需鉴权
        if (prefixMatcher.match(uri))
            return chain.filter(exchange);
        //TODO: 把snqg里的filter逻辑优化加进来
        //否则，校验用户是否有权限
        ServerHttpRequest request = exchange.getRequest();
        List<String> tokens = request.getHeaders().get(Headers.TOKEN_HEADER);
        String token = null;

        ThrowUtil.throwIf(Objects.isNull(tokens) || Objects.isNull(token = tokens.get(0)), ErrorCode.NOT_AUTHORIZED_ERROR, "请求未附带token");

        CompletableFuture<Response<Long>> future = userClientProvider.getIfAvailable().authorize(token, uri);

        return Mono.fromFuture(future)
                .timeout(Duration.ofSeconds(5))
                .onErrorComplete(
                        resp -> {
                            System.out.println("错误：" + resp);
                            return true;
                        }
                )
                .flatMap(
                authorizeResponse -> {

                    ThrowUtil.throwIf(authorizeResponse.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.NOT_AUTHORIZED_ERROR, "授权失败：用户无权限访问");

                    Long userId = authorizeResponse.getData();

                    ServerHttpRequest processedRequest = request.mutate()
                            .headers(h -> h.remove(Headers.TOKEN_HEADER))
                            .header(Headers.USERID_HEADER, String.valueOf(userId))
                            .build();

                    return chain.filter(exchange.mutate().request(processedRequest).build());
                }
        );
    }

    @Override
    public int getOrder() {
        return -10;
    }
}
