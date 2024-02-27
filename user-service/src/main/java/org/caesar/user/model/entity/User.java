package org.caesar.user.model.entity;

import lombok.Data;
import org.caesar.common.util.ListUtil;
import org.caesar.domain.constant.StrConstant;
import org.caesar.domain.user.enums.UserRole;
import org.caesar.domain.user.vo.AuthorizationVO;
import org.caesar.domain.user.request.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class User {

    public static final int NORMAL_USER = 1;

    /**
     * 主键，用户唯一标识
     */
    private Long id;

    /**
     * 用户名，不多于20个字符
     */
    private String username;

    /**
     * 密码，使用BCrypt加密
     */
    private String password;

    /**
     * 用户电话
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户微信id
     */
    private String wxid;

    /**
     * 用户QQid
     */
    private String qqid;

    /**
     * 用户头像路径
     */
    private String avatarUrl;

    /**
     * 创建账号时间
     */
    private LocalDateTime createTime;

    /**
     * 账号上次更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 账号状态：0:离线 1:在线 2:封禁中
     */
    private Integer state;

    /**
     * 逻辑删除字段：0：存在 1：删除
     */
    private Integer isDelete;

    /**
     * 用户角色
     */
    private List<Integer> roles;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static User register(long userId, RegisterRequest request) {

        User user = new User();
        user.setId(userId);

        // 设置认证身份信息
        switch (request.getMethod()) {
            case EMAIL:
                user.setEmail(request.getIdentity());
                break;
            case PHONE:
                user.setPhone(request.getIdentity());
                break;
            case WECHAT:
                user.setWxid(request.getIdentity());
                break;
            case QQ:
                user.setQqid(request.getIdentity());
                break;
        }

        String password = passwordEncoder.encode(request.getPassword());
        user.setUsername(request.getUsername());
        user.setPassword(password);
        //TODO: 随机设置默认头像
        user.setAvatarUrl(StrConstant.DEFAULT_AVATAR_URL);
        LocalDateTime now = LocalDateTime.now();
        user.setRoles(Collections.singletonList(NORMAL_USER));
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setState(0);

        return user;
    }

    public AuthorizationVO getAuthorization() {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        authorizationVO.setUserId(this.getId());
        authorizationVO.setRoles(this.getRoles());
        return authorizationVO;
    }
}
