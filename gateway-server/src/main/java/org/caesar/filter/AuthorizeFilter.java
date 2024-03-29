package org.caesar.filter;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.caesar.common.client.UserClient;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.log.LogUtil;
import org.caesar.common.resp.RespUtil;
import org.caesar.common.str.JwtUtil;
import org.caesar.common.str.StrUtil;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.constant.Headers;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.str.PrefixMatcher;
import org.caesar.domain.user.vo.AuthorizationVO;
import org.caesar.domain.user.vo.RoleVO;
import org.caesar.util.ExchangeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.scheduling.annotation.Scheduled;
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

//对请求进行认证和鉴权，并在请求上加上用户id
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    //TODO: 参数化配置白名单
    public static String[] AUTHORIZE_WHITE_LIST = {
            "/user-service/auth/login", "/user-service/user/sendCode/*",
            "/user-service/auth/refreshToken", "/user-service/auth/register", "/user-service/auth/captcha",
            "/user-service/auth/gen-captcha", "/user-service/auth/login-user", "/user-service/user/reset"};

    public static String[] TEST_WHITE_LIST = {
            "/user-service/auth/login", "/user-service/user/sendCode/*",
            "/user-service/auth/refreshToken", "/user-service/auth/register", "/user-service/user/reset",
            "/user-service/auth/captcha", "/user-service/auth/gen-captcha", "/user-service/auth/login-user",
            "/v2*", "/user-service/v2*", "/question-service/v2*", "/search-service/v2*", "/article-service/v2*"
    };

    private static PrefixMatcher whiteListMatcher = new PrefixMatcher(AUTHORIZE_WHITE_LIST);

    private static final Map<Integer, PrefixMatcher> authorizeMap = new ConcurrentHashMap<>();

    // 同步权限角色信息间隔时间（2小时）
    public static final long SYNC_INTERVAL = 3600 * 1000;

    @Resource
    private UserClient userClient;

    @Value("${application.env:prod}")
    private String env;

    @PostConstruct
    private void initAuthorizeMap() {

        String[] whiteList = "test".equals(env) ? TEST_WHITE_LIST : AUTHORIZE_WHITE_LIST;
        whiteListMatcher = new PrefixMatcher(whiteList);

        List<RoleVO> roles = RespUtil.handleWithThrow(userClient.getUpdatedRole(LocalDateTime.MIN),
                "[System Init] Fail to create userClient");
        for (RoleVO role : roles) {
            authorizeMap.put(role.getId(), new PrefixMatcher(role.getPermissions()));
        }
    }

    @Scheduled(fixedRate = SYNC_INTERVAL)
    public void syncRole() {
        List<RoleVO> roles = RespUtil.handleWithThrow(
                userClient.getUpdatedRole(
                        LocalDateTime.now().minusHours(SYNC_INTERVAL * 2)
                ),
                "[System Init] Fail to create userClient");
        for (RoleVO role : roles) {
            authorizeMap.put(role.getId(), new PrefixMatcher(role.getPermissions()));
        }
    }

    @Scheduled(fixedRate = SYNC_INTERVAL)
    public void syncWhiteList() {
        // TODO: 从数据库动态获取白名单
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String uri = exchange.getRequest().getURI().getPath();
        ContextHolder.setBusinessName(uri);

        System.out.println("visit uri:" + uri);

        //若在白名单中，则无需认证
        if (whiteListMatcher.match(uri))
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

        ThrowUtil.ifNull(token, ErrorCode.NOT_AUTHORIZED_ERROR, "unauthenticated request without token");

        Response<Long> authorizeResp = authorize(token, uri);

        if (ErrorCode.SUCCESS.getCode() != authorizeResp.getCode()) {
            return ExchangeUtil.returnError(exchange, authorizeResp);
        }

        Long userId = authorizeResp.getData();
        ContextHolder.setUserId(userId);

        ServerHttpRequest processedRequest = request.mutate()
                .headers(h -> h.remove(Headers.TOKEN_HEADER))
                .header(Headers.USERID_HEADER, String.valueOf(userId))
                .build();

        return chain.filter(exchange.mutate().request(processedRequest).build())
                .then(Mono.fromRunnable(ContextHolder::clear));
    }

    @Override
    public int getOrder() {
        return -10;
    }

    /**
     * @param token       用户凭证
     * @param requestPath 用户请求路径
     */
    public Response<Long> authorize(String token, String requestPath) {

        long userId;
        AuthorizationVO authorization;

        String jsonAuthorization = JwtUtil.getJwtSubject(token);

        //jwt不合法
        if (StrUtil.isBlank(jsonAuthorization)) {
            LogUtil.warn(ErrorCode.NOT_AUTHENTICATED_ERROR, "Token is illegal or expired, please login again.");
            return new Response<>(ErrorCode.NOT_AUTHENTICATED_ERROR, null, "token is illegal or expired, please login again");
        }

        authorization = JSON.parseObject(jsonAuthorization, AuthorizationVO.class);

        if (Objects.isNull(authorization))
            return new Response<>(ErrorCode.NOT_AUTHENTICATED_ERROR, null, "token is illegal or expired, please login again");

        userId = authorization.getUserId();
        List<Integer> roles = authorization.getRoles();

        for (Integer role : roles) {
            PrefixMatcher authorizeMatcher = authorizeMap.get(role);
            if (authorizeMatcher.match(requestPath)) return Response.ok(userId);
        }

        return new Response<>(ErrorCode.NOT_AUTHORIZED_ERROR, null, "user does not have the permission to access");
    }
}
