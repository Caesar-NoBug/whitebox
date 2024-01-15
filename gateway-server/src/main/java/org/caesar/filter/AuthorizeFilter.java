package org.caesar.filter;

import com.alibaba.fastjson.JSON;
import org.caesar.common.client.UserClient;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.log.LogUtil;
import org.caesar.common.resp.RespUtil;
import org.caesar.common.str.JwtUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.constant.Headers;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.str.PrefixMatcher;
import org.caesar.domain.user.vo.AuthorizationVO;
import org.caesar.domain.user.vo.RoleVO;
import org.caesar.util.ExchangeUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//TODO: 统一的异常处理机制
//对请求进行认证和鉴权，并在请求上加上用户id
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    // TODO: 加上游客查看文章的接口
    // TODO: 从数据库动态获取白名单
    public static final String[] AUTHORIZE_WHITE_LIST = {"/user-service/auth/login", "/user-service/auth/sendCode/**",
            "/user-service/auth/refreshToken", "/user-service/auth/register/**", "/user-service/user/reset"};

    private static final PrefixMatcher prefixMatcher = new PrefixMatcher(AUTHORIZE_WHITE_LIST);

    private static final Map<Integer, PrefixMatcher> authorizeMap = new ConcurrentHashMap<>();

    //@Resource
    //private ObjectProvider<UserClient> userClientProvider;
    //private UserClient userClient;

    //@Resource
    //private UserWebClient userWebClient;

    @Resource
    private UserClient userClient;

    @PostConstruct
    private void initAuthorizeMap() {
        //UserClient userCLient = userClientProvider.getIfAvailable();
        List<RoleVO> roles = RespUtil.handleWithThrow(userClient.getUpdatedRole(LocalDateTime.MIN),
                "[System Init] Fail to create userClient");
        for (RoleVO role : roles) {
            authorizeMap.put(role.getId(), new PrefixMatcher(role.getPermissions()));
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String uri = exchange.getRequest().getURI().getPath();
        System.out.println("visit uri:" + uri);
        //if(1 == 1) return ExchangeUtil.returnError(exchange, Response.error("hello world"));
        //若在白名单中，则无需认证
        if (prefixMatcher.match(uri))
            return chain.filter(
                    exchange.mutate()
                            .request(
                                    exchange.getRequest()
                                            .mutate()
                                            .headers(h -> h.remove(Headers.TOKEN_HEADER)).build())
                            .build()
            );

        //否则，校验用户是否有权限
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(Headers.TOKEN_HEADER);

        ThrowUtil.ifNull(token, ErrorCode.NOT_AUTHORIZED_ERROR, "illegal request without token");

        Long userId = authorize(token, uri);

        ServerHttpRequest processedRequest = request.mutate()
                .headers(h -> h.remove(Headers.TOKEN_HEADER))
                .header(Headers.USERID_HEADER, String.valueOf(userId))
                .build();

        return chain.filter(exchange.mutate().request(processedRequest).build());

        //CompletableFuture<Response<Long>> future = userClientProvider.getIfAvailable().authorize(token, uri);

        /*return Mono.fromFuture(future)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(resp -> {
                    // TODO: 改成日志调用
                    // System.out.println("错误：" + resp);
                    return Mono.error(new BusinessException(ErrorCode.NOT_AUTHORIZED_ERROR, "authentication failure"));
                })
                .flatMap(
                authorizeResponse -> {

                    ThrowUtil.ifTrue(authorizeResponse.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.NOT_AUTHORIZED_ERROR, "授权失败：用户无权限访问");

                    Long userId = authorizeResponse.getData();

                    ServerHttpRequest processedRequest = request.mutate()
                            .headers(h -> h.remove(Headers.TOKEN_HEADER))
                            .header(Headers.USERID_HEADER, String.valueOf(userId))
                            .build();

                    return chain.filter(exchange.mutate().request(processedRequest).build());
                }
        );*/
    }

    @Override
    public int getOrder() {
        return -10;
    }

    /**
     * @param token       用户凭证
     * @param requestPath 用户请求路径
     */
    public Long authorize(String token, String requestPath) {

        long userId;
        AuthorizationVO authorization;

        try {
            String jsonAuthorization = JwtUtil.getJwtSubject(token);
            authorization = JSON.parseObject(jsonAuthorization, AuthorizationVO.class);

        } catch (Exception e) {
            //jwt不合法
            LogUtil.warn("[Unauthenticated User] token is illegal or expired, please login again");
            throw new BusinessException(ErrorCode.NOT_AUTHENTICATED_ERROR, "token is illegal or expired, please login again");
        }

        if (Objects.isNull(authorization))
            throw new BusinessException(ErrorCode.NOT_AUTHENTICATED_ERROR, "token is illegal or expired, please login again");

        userId = authorization.getUserId();
        List<Integer> roles = authorization.getRoles();

        for (Integer role : roles) {
            PrefixMatcher authorizeMatcher = authorizeMap.get(role);
            if(authorizeMatcher.match(requestPath)) return userId;
        }

        throw new BusinessException(ErrorCode.NOT_AUTHORIZED_ERROR, "user does not have the permission to access");
    }

    //TODO: 定时同步角色和path的关系
}
