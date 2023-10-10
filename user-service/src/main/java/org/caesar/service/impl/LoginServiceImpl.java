package org.caesar.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.caesar.constant.RedisPrefix;
import org.caesar.model.dto.AuthUser;
import org.caesar.model.dto.TokenDTO;
import org.caesar.security.token.EmailAuthenticationToken;
import org.caesar.security.token.UsernameAuthenticationToken;
import org.caesar.service.LoginService;
import org.caesar.common.util.JwtUtil;
import org.caesar.common.util.RedisCache;
import org.caesar.common.util.StrUtil;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    public static final int DEFAULT_REFRESH_TOKEN_LENGTH = 32;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Map<String, Object> loginUsername(String username, String password) {

        UsernameAuthenticationToken authenticationToken = new UsernameAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return handleAuthentication(authentication);
    }

    @Override
    public Map<String, Object> loginEmail(String email, String code) {

        EmailAuthenticationToken authenticationToken = new EmailAuthenticationToken(email, code);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return handleAuthentication(authentication);
    }

    @Override
    public Map<String, Object> loginPhone(String phone, String code) {
        return null;
    }

    //处理认证结果
    private Map<String, Object> handleAuthentication(Authentication authentication){

        //认证失败
        if(Objects.isNull(authentication)){
            return null;
        }

        AuthUser authUser = (AuthUser) authentication.getDetails();

        long userId = authUser.getBaseUser().getId();

        String jwtToken = JwtUtil.createJWT(userId + "");
        String refreshToken = StrUtil.getRandStr(DEFAULT_REFRESH_TOKEN_LENGTH);

        authUser.setRefreshToken(refreshToken);

        redisCache.setCacheObject(RedisPrefix.LOGIN_USER + userId, authUser, 30, TimeUnit.DAYS);
        redisCache.setCacheObject(RedisPrefix.LOGIN_JWT + userId, jwtToken, 1, TimeUnit.HOURS);

        log.info("用户登录成功：" + userId);

        HashMap<String, Object> map = new HashMap<>();
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setRefreshToken(refreshToken);
        tokenDTO.setJwt(jwtToken);
        map.put("token", tokenDTO);
        map.put("user", authUser.getBaseUser());

        return map;
    }

}
