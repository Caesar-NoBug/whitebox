package org.caesar.repository;

import org.caesar.model.entity.User;

public interface UserRepository {

    User selectUserById(Long id);

    User selectUserByName(String username);

    User selectUserByEmail(String email);

    User selectUserByPhone(String phone);

    boolean containsSimilarBindUser(User user);

    boolean insertUser(User user);

    boolean removeUser(Long id);
}
