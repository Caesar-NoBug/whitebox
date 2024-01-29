package org.caesar.user.service;

import org.caesar.domain.user.vo.UserPreferVO;

/**
* @author caesar
* @description 针对表【sys_user_extra】的数据库操作Service
* @createDate 2023-12-16 10:14:06
*/
public interface UserExtraService {
    UserPreferVO getUserPrefer(long userId);
}
