package org.caesar.config;

import lombok.Data;

@Data
public class RateLimiterConfig {

    // 路径
    private String uri;

    // 单用户限流
    private Integer user;

    // 总限流
    private Integer total;
}
