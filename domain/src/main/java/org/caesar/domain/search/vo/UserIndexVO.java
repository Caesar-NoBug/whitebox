package org.caesar.domain.search.vo;

import lombok.Data;

@Data
public class UserIndexVO implements IndexVO {

    /**
     * 主键，用户唯一标识
     */
    private Long id;

    /**
     * 用户名，不多于20个字符
     */
    private String username;

}
