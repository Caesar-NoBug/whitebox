package org.caesar.domain.user.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleVO {

    /**
     *  角色id
     */
    private Integer id;

    /**
     *  权限
     */
    private List<String> permissions;
}
