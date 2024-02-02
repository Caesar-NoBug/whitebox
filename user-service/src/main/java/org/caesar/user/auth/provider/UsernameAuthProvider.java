package org.caesar.user.auth.provider;

import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.str.StrEncoder;
import org.caesar.domain.user.enums.AuthMethod;
import org.caesar.user.auth.AuthenticationProvider;
import org.caesar.user.model.entity.User;
import org.caesar.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UsernameAuthProvider implements AuthenticationProvider {

    @Resource
    private UserRepository userRepo;

    @Override
    public void authenticate(String username, String password) {

        User user = userRepo.selectUserByName(username);

        ThrowUtil.ifNull(user,"Authenticate failed: invalid username, user does not exist.");

        ThrowUtil.ifTrue(!StrEncoder.match(password, user.getPassword()), "Authenticate failed: wrong password.");
    }

    @Override
    public User getIdenticalUser(String username) {
        return userRepo.selectUserByName(username);
    }

    @Override
    public AuthMethod getMethod() {
        return AuthMethod.USERNAME;
    }
}
