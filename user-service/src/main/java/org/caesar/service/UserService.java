package org.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.caesar.model.dto.AuthUser;
import org.caesar.model.entity.BaseUser;
import org.caesar.model.vo.Response;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

/**
* @author caesar
* @description 针对表【sys_user_base】的数据库操作Service
* @createDate 2023-05-01 09:36:22
*/
public interface UserService extends IService<BaseUser> {

    BaseUser selectBaseUserById(Long id);

    //根据用户查询带权限信息的用户信息
    AuthUser selectAuthUserByEmail(String email);

    //根据用户查询带权限信息的用户信息
    AuthUser selectAuthUserById(Long id);

    AuthUser selectAuthUserByUsername(String username);

    //根据用户查询带权限信息的用户信息
    AuthUser selectAuthUserByPhone(String phone);

    //校验用户是否有权限访问该接口
    Response<String> authorize(String jwt, String requestPath);

    Response refreshToken(long userId, String refreshToken, LocalDateTime lastUpdateTime);

    Response logout(String jwt);

    Response register(BaseUser baseUser);
}
