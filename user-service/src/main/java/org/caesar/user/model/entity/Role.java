package org.caesar.user.model.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Role {

    /**
     *  角色id
     */
    private Integer id;

    /**
     *  权限
     */
    private List<String> permissions;

    public void addPermission(String permission) {

        if (permissions == null) {
            permissions = new ArrayList<>();
        }

        permissions.add(permission);
    }

}
