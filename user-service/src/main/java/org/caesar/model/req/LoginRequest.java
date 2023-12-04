package org.caesar.model.req;


import lombok.Data;
import org.caesar.enums.AuthenticationMethod;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {

    /**
     *  用户身份
     */
    private String identity;

    /**
     *  登录凭证
     * */
    @NotNull
    private String credential;

    /**
     * 登录方式
     */
    @NotNull(message = "登录失败，非法参数：未指定登录方式")
    private AuthenticationMethod method;
}
