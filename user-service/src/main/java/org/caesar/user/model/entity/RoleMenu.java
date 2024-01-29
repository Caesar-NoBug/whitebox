package org.caesar.user.model.entity;

import lombok.Data;

@Data
public class RoleMenu {

    /**
     *  角色id
     */
    private Integer id;

    /**
     *  权限
     */
    private String permission;
}
