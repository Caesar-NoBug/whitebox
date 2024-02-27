package org.caesar.user.repository.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.caesar.common.log.Logger;
import org.caesar.user.mapper.BaseUserMapper;
import org.caesar.user.mapper.MenuMapper;
import org.caesar.user.model.MsUserStruct;
import org.caesar.user.model.entity.Role;
import org.caesar.user.model.entity.RoleMenu;
import org.caesar.user.model.entity.User;
import org.caesar.user.model.po.UserPO;
import org.caesar.user.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl extends ServiceImpl<BaseUserMapper, UserPO> implements UserRepository {

    @Resource
    private BaseUserMapper userMapper;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private MsUserStruct userStruct;

    @Logger(value = "/[Repo] selectUserById")
    @Override
    public User selectUserById(Long id) {
        return loadUserWithPermissions(userMapper.selectById(id));
    }

    @Logger(value = "/[Repo] selectUserMinByIds")
    @Override
    public List<User> selectUserMinByIds(List<Long> ids) {
        return userMapper.selectBatchIds(ids).stream()
                .map(userStruct::POtoDO).collect(Collectors.toList());
    }

    @Logger(value = "/[Repo] selectUserByName")
    @Override
    public User selectUserByName(String username) {
        return loadUserWithPermissions(userMapper.selectByUsername(username));
    }

    @Logger(value = "/[Repo] selectUserByEmail")
    @Override
    public User selectUserByEmail(String email) {
        return loadUserWithPermissions((userMapper.selectByEmail(email)));
    }

    @Logger(value = "/[Repo] selectUserByPhone")
    @Override
    public User selectUserByPhone(String phone) {
        return loadUserWithPermissions((userMapper.selectByPhone(phone)));
    }

    @Logger(value = "/[Repo] containsSimilarBindUser")
    @Override
    public boolean containsSimilarBindUser(User user) {
        return userMapper.selectSimilarUserCount(userStruct.DOtoPO(user)) > 0;
    }

    @Transactional
    @Logger(value = "/[Repo] insertUser", args = true, result = true)
    @Override
    public boolean insertUser(User user) {
        return userMapper.insertUser(userStruct.DOtoPO(user)) > 0
                && menuMapper.insertUserRole(user.getId(), user.getRoles());
    }

    @Logger(value = "/[Repo] removeUser", args = true)
    @Override
    public boolean removeUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Logger(value = "/[Repo] getUpdatedUser", result = true)
    @Override
    public List<Role> getUpdatedRoles(LocalDateTime updateTime) {

        List<RoleMenu> roleMenuList = menuMapper.getUpdatedRole(updateTime);

        if (CollectionUtils.isEmpty(roleMenuList)) return null;

        List<Role> roles = new ArrayList<>();

        Map<Integer, List<RoleMenu>> roleMenus = roleMenuList.stream()
                .collect(Collectors.groupingBy(RoleMenu::getId));

        roleMenus.forEach((id, roleMenu) -> {
            Role role = new Role();
            role.setId(id);
            role.setPermissions(
                    roleMenu.stream()
                            .map(RoleMenu::getPermission)
                            .collect(Collectors.toList()));
            roles.add(role);
        });

        return roles;
    }

    //封装权限信息，并将PO转换成DO
    private User loadUserWithPermissions(UserPO userPO) {

        User user = userStruct.POtoDO(userPO);

        if (Objects.isNull(user)) return null;

        List<Integer> roles = menuMapper.selectRolesByUserId(userPO.getId());

        user.setRoles(roles);

        return user;
    }

}
