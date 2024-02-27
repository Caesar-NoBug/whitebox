package org.caesar.domain.user.request;


import lombok.Data;
import org.caesar.domain.user.enums.AuthMethod;


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
     *   人机校验id
     */
    private String captchaId;

    /**
     * 登录方式
     */
    @NotNull(message = "登录失败，非法参数：未指定登录方式")
    private AuthMethod method;
}
