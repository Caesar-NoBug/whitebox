package org.caesar.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    //用于刷新jwt token
    private String refreshToken;
    //用于认证用户
    private String jwt;
    //用于判断用户信息是否需要更新
    private LocalDateTime lastUpdateTime;
}
