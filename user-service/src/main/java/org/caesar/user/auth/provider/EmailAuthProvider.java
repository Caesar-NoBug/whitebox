package org.caesar.user.auth.provider;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.str.StrUtil;
import org.caesar.user.constant.CacheKey;
import org.caesar.domain.common.enums.StrFormat;
import org.caesar.domain.user.enums.AuthMethod;
import org.caesar.user.auth.AuthenticationProvider;
import org.caesar.user.model.entity.User;
import org.caesar.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EmailAuthProvider implements AuthenticationProvider {

    @Resource
    private UserRepository userRepo;

    @Resource
    private CacheRepository cacheRepo;

    @Override
    public void authenticate(String email, String code) {

        ThrowUtil.ifTrue(StrUtil.checkFormat(email, StrFormat.EMAIL) || email.contains("+"), "Authenticate failed: invalid email format");

        String cacheKey = CacheKey.AUTH_CODE_EMAIL + email;

        String realCode = cacheRepo.getObject(cacheKey);

        ThrowUtil.ifTrue(!code.equals(realCode), "Authenticate failed: invalid validation code or validation code expired.");

        // 认证成功则删除验证码
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
}
