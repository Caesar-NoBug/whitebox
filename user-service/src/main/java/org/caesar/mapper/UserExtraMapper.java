package org.caesar.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.caesar.model.po.UserExtraPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author caesar
* @description 针对表【sys_user_extra】的数据库操作Mapper
* @createDate 2023-12-16 10:14:06
* @Entity org.caesar.model.po.UserExtra
*/
@Mapper
public interface UserExtraMapper extends BaseMapper<UserExtraPO> {

}




