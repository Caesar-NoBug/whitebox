package org.caesar.user.repository;

import org.caesar.user.model.entity.Role;
import org.caesar.user.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository {

    User selectUserById(Long id);

    List<User> selectUserByIds(List<Long> ids);

    User selectUserByName(String username);

    User selectUserByEmail(String email);

    User selectUserByPhone(String phone);

    boolean containsSimilarBindUser(User user);

    boolean insertUser(User user);

    boolean removeUser(Long id);

    List<Role> getUpdatedRoles(LocalDateTime updateTime);
}
