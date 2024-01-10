package org.caesar.domain.user.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
public class UserMinVO implements Serializable {

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
