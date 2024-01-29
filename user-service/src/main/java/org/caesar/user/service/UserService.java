package org.caesar.user.service;

import org.caesar.domain.user.vo.RoleVO;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.domain.user.request.RegisterRequest;
import org.caesar.domain.user.request.LoginRequest;
import org.caesar.domain.user.vo.UserVO;

import javax.servlet.http.HttpSession;
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

    UserVO register(RegisterRequest request);

    String refreshToken(long userId, String refreshToken);

    Map<Long, UserMinVO> getUserMin(List<Long> userId);

    // 获取更新的权限角色信息
    List<RoleVO> getUpdatedRole(LocalDateTime updateTime);
    //TODO: update User 时同步删除userMin缓存
}
