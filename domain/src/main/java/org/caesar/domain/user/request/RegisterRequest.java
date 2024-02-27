package org.caesar.domain.user.request;

import lombok.Data;
import org.caesar.domain.user.enums.AuthMethod;

import javax.validation.constraints.NotNull;

@Data
public class RegisterRequest {

    public static final String DEFAULT_MESSAGE = "Incomplete register information, please fill in complete information";

    /**
     *  用户名
     */
    @NotNull(message = DEFAULT_MESSAGE)
    private String username;

    /**
     * 密码
     */
    @NotNull(message = DEFAULT_MESSAGE)
    private String password;

    /**
     * 认证方式
     */
    @NotNull(message = DEFAULT_MESSAGE)
    private AuthMethod method;

    /**
     * 身份标识
     */
    @NotNull(message = DEFAULT_MESSAGE)
    private String identity;

    /**
     * 认证凭证
     */
    @NotNull(message = DEFAULT_MESSAGE)
    private String credential;

    /**
     *   人机校验id
     */
    private String captchaId;
}
