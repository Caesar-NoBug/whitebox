package org.caesar.domain.user.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户身份、权限信息
 */
@Data
public class AuthorizationVO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户角色
     */
    private List<Integer> roles;
}
