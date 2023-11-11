package org.caesar.model.req;

import lombok.Data;
import org.caesar.enums.AuthenticationMethod;

@Data
public class RegisterRequest {

    /**
     *  用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 认证方式
     */
    private AuthenticationMethod method;

    /**
     * 身份标识
     */
    private String identity;

    /**
     * 认证凭证
     */
    private String credential;

}
