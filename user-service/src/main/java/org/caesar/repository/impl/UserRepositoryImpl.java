package org.caesar.repository.impl;

import jakarta.annotation.Resource;
import org.caesar.mapper.BaseUserMapper;
import org.caesar.mapper.MenuMapper;
import org.caesar.model.MsUserStruct;
import org.caesar.model.entity.User;
import org.caesar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Resource
    private BaseUserMapper userMapper;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private MsUserStruct userStruct;

    @Override
    public User selectUserById(Long id) {
        return userStruct.POtoDO(userMapper.selectById(id));
    }

    @Override
    public User selectUserByName(String username) {
        return loadUserWithPermissions(
                userStruct.POtoDO(userMapper.selectByUsername(username))
        );
    }

    @Override
    public User selectUserByEmail(String email) {
        return loadUserWithPermissions(
                userStruct.POtoDO(userMapper.selectByEmail(email))
        );
    }

    @Override
    public User selectUserByPhone(String phone) {
        return loadUserWithPermissions(
                userStruct.POtoDO(userMapper.selectByPhone(phone))
        );
    }

    @Override
    public boolean containsSimilarBindUser(User user) {
        return userMapper.selectSimilarUserCount(userStruct.DOtoPO(user)) > 0;
    }

    @Override
    public boolean insertUser(User user) {
        //TODO: 插入时同时插入角色信息
        return userMapper.insertUser(userStruct.DOtoPO(user)) > 0
                && menuMapper.insertUserRole(user.getId(), user.getRoles());
    }

    @Override
    public boolean removeUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    //封装权限信息
    private User loadUserWithPermissions(User user) {

        if (Objects.isNull(user)) return null;

        List<Integer> roles = menuMapper.selectRolesByUserId(user.getId());
        user.setRoles(roles);

        return user;
    }
}
