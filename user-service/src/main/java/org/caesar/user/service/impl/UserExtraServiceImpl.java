package org.caesar.user.service.impl;

import org.caesar.domain.user.vo.UserPreferVO;
import org.caesar.user.model.MsUserExtraStruct;
import org.caesar.user.repository.UserExtraRepository;
import org.caesar.user.service.UserExtraService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author caesar
* @description 针对表【sys_user_extra】的数据库操作Service实现
* @createDate 2023-12-16 10:14:06
*/
@Service
public class UserExtraServiceImpl implements UserExtraService{

    @Resource
    private UserExtraRepository userExtraRepo;

    @Resource
    private MsUserExtraStruct userExtraStruct;

    @Override
    public UserPreferVO getUserPrefer(long userId) {
        return userExtraStruct.DOtoPreferVO(userExtraRepo.getUserPrefer(userId));
    }

}




