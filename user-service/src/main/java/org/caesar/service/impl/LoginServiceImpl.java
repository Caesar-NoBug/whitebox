package org.caesar.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class LoginServiceImpl {



   /* @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public UserVO loginUsername(String username, String password) {

        UsernameAuthenticationToken authenticationToken = new UsernameAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return handleAuthentication(authentication);
    }

    @Override
    public UserVO loginEmail(String email, String code) {

        EmailAuthenticationToken authenticationToken = new EmailAuthenticationToken(email, code);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return handleAuthentication(authentication);
    }

    @Override
    public UserVO loginPhone(String phone, String code) {
        return null;
    }*/

    //处理认证结果
    private Map<String, Object> handleAuthentication(Authentication authentication){

        /*//认证失败
        if(Objects.isNull(authentication)){
            return null;
        }

        UserDTO authUser = (UserDTO) authentication.getDetails();

        long userId = authUser.getUserPO().getId();

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
        map.put("user", authUser.getUserPO());

        return map;*/
        return null;
    }

}
