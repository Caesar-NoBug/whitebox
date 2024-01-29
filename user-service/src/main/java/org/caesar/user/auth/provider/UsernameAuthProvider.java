package org.caesar.user.auth.provider;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.str.StrEncoder;
import org.caesar.domain.user.enums.AuthMethod;
import org.caesar.user.auth.AuthenticationProvider;
import org.caesar.user.model.entity.User;
import org.caesar.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernameAuthProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepo;

    @Override
    public void authenticate(String username, String password) {

        ThrowUtil.testStr(username, "用户名非法");

        User user = userRepo.selectUserByName(username);

        ThrowUtil.ifNull(user,"认证失败：用户名错误，该用户不存在！");

        ThrowUtil.ifTrue(!StrEncoder.match(password, user.getPassword()), "认证失败：密码错误");
    }

    @Override
    public User getIdenticalUser(String username) {
        return userRepo.selectUserByName(username);
    }

    @Override
    public AuthMethod getMethod() {
        return AuthMethod.USERNAME;
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