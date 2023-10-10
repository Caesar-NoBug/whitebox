package org.caesar.security.provider;

import org.caesar.model.dto.AuthUser;
import org.caesar.security.token.UsernameAuthenticationToken;
import org.caesar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UsernameAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
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
    }

}
