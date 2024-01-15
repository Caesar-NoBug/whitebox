package org.caesar.repository;

import org.caesar.model.entity.Role;
import org.caesar.model.entity.RoleMenu;
import org.caesar.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
