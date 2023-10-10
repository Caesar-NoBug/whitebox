package org.caesar.security.provider;

import org.caesar.constant.RedisPrefix;
import org.caesar.model.dto.AuthUser;
import org.caesar.security.token.EmailAuthenticationToken;
import org.caesar.service.UserService;
import org.caesar.common.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EmailAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = (String) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();

        String redisKey = RedisPrefix.LOGIN_CODE_EMAIL + email;

        String realCode = redisCache.getCacheObject(redisKey);

        //验证码过期或验证码错误
        if(Objects.isNull(realCode) || !realCode.equals(code))
            return null;

        AuthUser authUser = userService.selectAuthUserByEmail(email);

        //认证通过,使验证码过期
        redisCache.deleteObject(redisKey);

        return new EmailAuthenticationToken(authUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
