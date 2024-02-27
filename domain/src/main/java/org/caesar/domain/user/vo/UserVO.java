package org.caesar.domain.user.vo;

import lombok.Data;
import org.caesar.domain.user.enums.UserRole;

import java.util.List;

@Data
public class UserVO {

    /**
     *  用户id
     */
    private long userId;

    /**
     * 用户认证信息，包含用户id，用户role（权限）
     */
    private String token;

    /**
     * 用于刷新token
     */
    private String refreshToken;

    /**
     * 用户权限角色
     */
    private List<UserRole> roles;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatar;
}
