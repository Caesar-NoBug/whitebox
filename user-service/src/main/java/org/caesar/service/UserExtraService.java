package org.caesar.service;

import org.caesar.domain.user.vo.UserPreferVO;
import org.caesar.model.entity.UserExtra;
import org.caesar.model.po.UserExtraPO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author caesar
* @description 针对表【sys_user_extra】的数据库操作Service
* @createDate 2023-12-16 10:14:06
*/
public interface UserExtraService {
    UserPreferVO getUserPrefer(long userId);
}
