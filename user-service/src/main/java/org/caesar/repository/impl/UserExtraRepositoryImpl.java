package org.caesar.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.mapper.UserExtraMapper;
import org.caesar.model.MsUserExtraStruct;
import org.caesar.model.entity.UserExtra;
import org.caesar.model.po.UserExtraPO;
import org.caesar.repository.UserExtraRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class UserExtraRepositoryImpl extends ServiceImpl<UserExtraMapper, UserExtraPO> implements UserExtraRepository {

    @Resource
    private MsUserExtraStruct userExtraStruct;

    @Override
    public UserExtra getById(long userId) {
        return userExtraStruct.POtoDO(baseMapper.selectById(userId));
    }

    @Override
    public UserExtra getUserPrefer(long userId) {
        QueryWrapper<UserExtraPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(UserExtraPO.Fields.preference, UserExtraPO.Fields.occupation)
                .eq(UserExtraPO.Fields.id, userId);
        return userExtraStruct.POtoDO(baseMapper.selectOne(queryWrapper));
    }

}
