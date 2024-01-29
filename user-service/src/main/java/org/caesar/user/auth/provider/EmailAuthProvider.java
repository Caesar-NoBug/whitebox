package org.caesar.user.auth.provider;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.repository.CacheRepository;
import org.caesar.common.str.StrUtil;
import org.caesar.user.constant.CacheKey;
import org.caesar.domain.common.enums.StrFormat;
import org.caesar.domain.user.enums.AuthMethod;
import org.caesar.user.auth.AuthenticationProvider;
import org.caesar.user.model.entity.User;
import org.caesar.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailAuthProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CacheRepository cacheRepo;

    @Override
    public void authenticate(String email, String code) {

        ThrowUtil.ifTrue(StrUtil.checkFormat(email, StrFormat.EMAIL), "认证失败：非法邮箱格式");

        String cacheKey = CacheKey.AUTH_CODE_EMAIL + email;

        String realCode = cacheRepo.getObject(cacheKey);

        ThrowUtil.ifTrue(!code.equals(realCode), "认证失败：验证码已失效或验证码错误");

        cacheRepo.deleteObject(cacheKey);
    }

    @Override
    public User getIdenticalUser(String email) {
        return userRepo.selectUserByEmail(email);
    }

    @Override
    public AuthMethod getMethod() {
        return AuthMethod.EMAIL;
    }

    /*@Override
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
    */
}
