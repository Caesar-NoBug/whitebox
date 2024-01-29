package org.caesar.user.auth.provider;

import org.caesar.domain.user.enums.AuthMethod;
import org.caesar.user.auth.AuthenticationProvider;
import org.caesar.user.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PhoneAuthProvider implements AuthenticationProvider {

    @Override
    public void authenticate(String phone, String code) {

    }

    @Override
    public User getIdenticalUser(String phone) {
        return null;
    }

    @Override
    public AuthMethod getMethod() {
        return AuthMethod.PHONE;
    }
}
