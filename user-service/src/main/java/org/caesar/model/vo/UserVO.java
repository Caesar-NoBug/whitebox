package org.caesar.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class UserVO {

    /**
     * 用户认证信息，包含用户id，用户role（权限）
     */
    private String token;

    /**
     * 用于刷新token
     */
    private String refreshToken;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatar;
}
