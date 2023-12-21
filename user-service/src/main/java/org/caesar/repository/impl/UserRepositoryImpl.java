package org.caesar.repository.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.mapper.BaseUserMapper;
import org.caesar.mapper.MenuMapper;
import org.caesar.model.MsUserStruct;
import org.caesar.model.entity.User;
import org.caesar.model.po.UserPO;
import org.caesar.repository.UserRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl extends ServiceImpl<BaseUserMapper, UserPO> implements UserRepository {

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

    //封装权限信息，并将PO转换成DO
    private User loadUserWithPermissions(UserPO userPO) {

        User user = userStruct.POtoDO(userPO);

        if(Objects.isNull(user)) return null;

        List<Integer> roles = menuMapper.selectRolesByUserId(userPO.getId());

        user.setRoles(roles);

        return user;
    }

}
