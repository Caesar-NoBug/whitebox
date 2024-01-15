package org.caesar.repository.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.lang.Collections;
import org.apache.ibatis.session.SqlSessionFactory;
import org.caesar.mapper.BaseUserMapper;
import org.caesar.mapper.MenuMapper;
import org.caesar.model.MsUserStruct;
import org.caesar.model.entity.Role;
import org.caesar.model.entity.RoleMenu;
import org.caesar.model.entity.User;
import org.caesar.model.po.UserPO;
import org.caesar.repository.UserRepository;
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
    private SqlSessionFactory sqlSessionFactory;

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
    public List<User> selectUserByIds(List<Long> ids) {
        return userMapper.selectBatchIds(ids).stream()
                .map(userStruct::POtoDO).collect(Collectors.toList());
    }

    @Override
    public User selectUserByName(String username) {
        return loadUserWithPermissions(userMapper.selectByUsername(username));
    }

    @Override
    public User selectUserByEmail(String email) {
        return loadUserWithPermissions((userMapper.selectByEmail(email)));
    }

    @Override
    public User selectUserByPhone(String phone) {
        return loadUserWithPermissions((userMapper.selectByPhone(phone)));
    }

    @Override
    public boolean containsSimilarBindUser(User user) {
        return userMapper.selectSimilarUserCount(userStruct.DOtoPO(user)) > 0;
    }

    @Transactional
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
