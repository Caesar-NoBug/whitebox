package com.caesar.mapper;


import com.caesar.model.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM tbl_user_base WHERE id = #{id}")
    User getUserById(@Param("id") int id);

    @Select("SELECT * FROM tbl_user_base WHERE username = #{username}")
    User getUserByUsername(@Param("username") String username);

    @Select("SELECT * FROM tbl_user_base WHERE phone = #{phone}")
    User getUserByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM tbl_user_base WHERE email = #{email}")
    User getUserByEmail(@Param("email") String email);
}
