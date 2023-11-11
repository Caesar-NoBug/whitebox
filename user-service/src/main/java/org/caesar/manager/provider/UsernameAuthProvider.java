package org.caesar.manager.provider;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.util.StrEncoder;
import org.caesar.domain.constant.enums.ErrorCode;
import org.caesar.enums.AuthenticationMethod;
import org.caesar.manager.AuthenticationProvider;
import org.caesar.model.entity.User;
import org.caesar.repository.UserRepository;
import org.caesar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UsernameAuthProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepo;

    @Override
    public void authenticate(String username, String password) {

        ThrowUtil.throwTestStr(username, "用户名非法");

        User user = userRepo.selectUserByName(username);

        ThrowUtil.throwIfNull(user,"认证失败：用户名错误，该用户不存在！");

        ThrowUtil.throwIf(!StrEncoder.match(password, user.getPassword()), "认证失败：密码错误");
    }

    @Override
    public User getIdenticalUser(String username) {
        return userRepo.selectUserByName(username);
    }

    @Override
    public AuthenticationMethod getMethod() {
        return AuthenticationMethod.USERNAME;
    }

   /* @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        AuthUser authUser = userService.selectAuthUserByUsername(username);

        //认证失败
        if(Objects.isNull(authUser) || !passwordEncoder.matches(password, authUser.getPassword())){
            return null;
        }

        UsernameAuthenticationToken usernameAuthenticationToken = new UsernameAuthenticationToken(authUser);

        return usernameAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernameAuthenticationToken.class.isAssignableFrom(authentication);
    }*/

}
