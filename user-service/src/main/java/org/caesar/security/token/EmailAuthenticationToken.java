package org.caesar.security.token;
import org.caesar.model.dto.UserDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.Collections;



public class EmailAuthenticationToken extends AbstractAuthenticationToken {
    private String email;
    private String code;
    private UserDTO user;

    public EmailAuthenticationToken(UserDTO user) {
        super(user.getAuthorities());
        setDetails(user);
    }

    public EmailAuthenticationToken(String email, String code) {
        super(Collections.EMPTY_LIST);
        this.email = email;
        this.code = code;
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }

}
