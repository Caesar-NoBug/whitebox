package org.caesar.security.token;

import org.caesar.model.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class UsernameAuthenticationToken  extends AbstractAuthenticationToken {
    private String username;
    private String password;

    public UsernameAuthenticationToken(AuthUser user) {
        super(user.getAuthorities());
        setDetails(user);
    }

    public UsernameAuthenticationToken(String username, String password) {
        super(Collections.EMPTY_LIST);
        this.username = username;
        this.password = password;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

}
