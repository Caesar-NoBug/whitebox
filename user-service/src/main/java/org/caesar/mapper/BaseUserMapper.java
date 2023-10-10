package org.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.caesar.model.entity.BaseUser;

/**
* @author caesar
* @description 针对表【sys_user_base】的数据库操作Mapper
* @createDate 2023-05-01 09:36:22
* @Entity org.caesar.model.entity.BaseUser
*/
@Mapper
public interface BaseUserMapper extends BaseMapper<BaseUser> {

    @Select("SELECT * FROM sys_user_base WHERE id = #{id}")
    BaseUser selectById(Long id);

    @Select("SELECT * FROM sys_user_base WHERE username = #{username}")
    BaseUser selectByUsername(String username);

    @Select("SELECT * FROM sys_user_base WHERE email = #{email}")
    BaseUser selectByEmail(String email);

    @Select("SELECT * FROM sys_user_base WHERE phone = #{phone}")
    BaseUser selectByPhone(String phone);

    //查询有相同邮箱、手机号、
    int selectSimilarUserCount(BaseUser baseUser);

    int insertUser(BaseUser baseUser);
}




