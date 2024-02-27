package org.caesar.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    // 普通用户
    USER(1, "USER"),
    // 管理员
    ADMIN(2, "ADMIN"),
    // 超级管理员
    SUPER_ADMIN(3, "SUPER_ADMIN");

    private final Integer code;
    private final String name;

    public static UserRole of(Integer code) {
        if(code < 0 || code > UserRole.values().length) return null;
        return UserRole.values()[code - 1];
    }

}
