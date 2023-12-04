package org.caesar.auth;

import org.caesar.enums.AuthenticationMethod;
import org.caesar.model.entity.User;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证管理器，统一处理认证逻辑
 */
@Component
public class AuthenticationManager implements ApplicationContextAware {

    private final Map<AuthenticationMethod, AuthenticationProvider> providerMap = new ConcurrentHashMap<>();

    /**
     * @param method 认证方式
     * @param identity 用户身份标识
     * @param credential 用户认证凭证
     * 认证用户信息，认证失败则抛出异常
     */
    public void authenticate(AuthenticationMethod method, String identity, String credential) {
        providerMap.get(method).authenticate(identity, credential);
    }

    /**
     * @param method 认证方式
     * @param identity 用户身份标识
     * @return 身份标识对应的用户
     */
    public User getIdenticalUser(AuthenticationMethod method, String identity) {
        return providerMap.get(method).getIdenticalUser(identity);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, AuthenticationProvider> tempMap = applicationContext.getBeansOfType(AuthenticationProvider.class);
        tempMap.values().forEach(provider -> providerMap.put(provider.getMethod(), provider));
    }

}
