package org.caesar.user.auth;

import org.caesar.common.exception.BusinessException;
import org.caesar.common.cache.CacheRepository;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.user.enums.AuthMethod;
import org.caesar.user.constant.CacheKey;
import org.caesar.user.model.entity.User;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 认证管理器，统一处理认证逻辑
 */
@Component
public class AuthenticationManager implements ApplicationContextAware {

    @Resource
    private CacheRepository cacheRepo;

    private final Map<AuthMethod, AuthenticationProvider> providerMap = new ConcurrentHashMap<>();

    private final int MAX_AUTHENTICATION_RETRY = 3;

    /**
     * @param method 认证方式
     * @param identity 用户身份标识
     * @param credential 用户认证凭证
     * 认证用户信息，认证失败则抛出异常
     */
    public void authenticate(AuthMethod method, String identity, String credential) {

        // 若非QQ或微信认证方式，则进行尝试认证，当多次认证失败时标记对应的身份标识为风险用户，拒绝认证
        if(!AuthMethod.QQ.equals(method) && !AuthMethod.WECHAT.equals(method))
            tryAuthenticate(method, identity, credential);
        else
            providerMap.get(method).authenticate(identity, credential);
    }

    /**
     * @param method 认证方式
     * @param identity 用户身份标识
     * @return 身份标识对应的用户
     */
    public User getIdenticalUser(AuthMethod method, String identity) {
        return providerMap.get(method).getIdenticalUser(identity);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, AuthenticationProvider> tempMap = applicationContext.getBeansOfType(AuthenticationProvider.class);
        tempMap.values().forEach(provider -> providerMap.put(provider.getMethod(), provider));
    }

    private void tryAuthenticate(AuthMethod method, String identity, String credential) {

        String cacheKey = String.format(CacheKey.AUTH_TIME, method, identity);
        Integer retryTime = cacheRepo.getObject(cacheKey);

        if(Objects.isNull(retryTime)) retryTime = 0;

        // 尝试次数过多直接抛出异常
        if(retryTime == MAX_AUTHENTICATION_RETRY) {
            throw new BusinessException(ErrorCode.NOT_AUTHENTICATED_ERROR, "Too many authenticate retries of " + identity + " with " + method);
        }

        try {
            providerMap.get(method).authenticate(identity, credential);
        } catch (Throwable e) {
            cacheRepo.setObject(cacheKey, retryTime + 1, 5, TimeUnit.MINUTES);
            throw e;
        }
    }
}
