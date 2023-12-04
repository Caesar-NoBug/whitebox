package org.caesar.domain.user.vo;

import lombok.Data;
import lombok.ToString;

@Data
public class UserMinVO {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatar;
}
