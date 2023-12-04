package org.caesar.auth;


import org.caesar.enums.AuthenticationMethod;
import org.caesar.model.entity.User;

/**
 * 认证提供者，负责认证用户身份
 */
public interface AuthenticationProvider {

    /**
     * @param identity 用户身份标识
     * @param credential 用户认证凭证
     * 认证用户信息，认证失败则抛出异常
     */
    void authenticate(String identity, String credential);

    /**
     * @param identity 用户身份标识
     * @return 身份标识对应的用户
     */
    User getIdenticalUser(String identity);

    /**
     * @return 认证器对应的认证方式
     */
    AuthenticationMethod getMethod();
}
