package org.caesar.auth.provider;

import org.caesar.enums.AuthenticationMethod;
import org.caesar.auth.AuthenticationProvider;
import org.caesar.model.entity.User;
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
    public AuthenticationMethod getMethod() {
        return AuthenticationMethod.PHONE;
    }
}
