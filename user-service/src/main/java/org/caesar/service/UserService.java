package org.caesar.service;

import org.caesar.domain.user.vo.RoleVO;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.model.req.RegisterRequest;
import org.caesar.model.req.LoginRequest;
import org.caesar.model.vo.UserVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
* @author caesar
* @description 针对表【sys_user_base】的数据库操作Service
* @createDate 2023-05-01 09:36:22
*/
public interface UserService {

    UserVO login(LoginRequest request);

    //校验用户是否有权限访问该接口,并返回用户id
    Long authorize(String jwt, String requestPath);

    String refreshToken(long userId, String refreshToken, LocalDateTime lastUpdateTime);

    void logout(String jwt);

    UserVO register(RegisterRequest request);

    Map<Long, UserMinVO> getUserMin(List<Long> userId);

    // 获取更新的权限角色信息
    List<RoleVO> getUpdatedRole(LocalDateTime updateTime);
    //TODO: update User 时同步删除userMin缓存
}
