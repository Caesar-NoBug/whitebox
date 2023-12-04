package org.caesar.model.entity;

import lombok.Data;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.repository.CacheRepository;
import org.caesar.domain.constant.StrConstant;
import org.caesar.common.str.StrUtil;
import org.caesar.constant.RedisPrefix;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.model.req.RegisterRequest;
import org.caesar.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class User {

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

    public User registerByEmail(RegisterRequest request, UserRepository userRepo, CacheRepository cacheRepo) {

        User user = new User();
        user.setEmail(request.getIdentity());
        boolean containsSimilar = userRepo.containsSimilarBindUser(user);

        ThrowUtil.ifTrue(containsSimilar, ErrorCode.ALREADY_EXIST_ERROR, "注册失败：已存在相同用户名或邮箱");

        String redisKey = RedisPrefix.REGISTER_CODE_EMAIL + request.getIdentity();
        String registerCode = (String) cacheRepo.getObject(redisKey);

        ThrowUtil.ifTrue(StrUtil.isBlank(registerCode), "注册失败：验证码已失效，请重新发送验证码");

        ThrowUtil.ifTrue(!registerCode.equals(request.getCredential()), "注册失败：验证码错误，请重新输入验证码");

        String password = passwordEncoder.encode(request.getPassword());

        user.setPassword(password);
        user.setId(cacheRepo.nextId(RedisPrefix.USER_INC_ID));
        //TODO: 随机设置默认头像
        user.setAvatarUrl(StrConstant.DEFAULT_AVATAR_URL);
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setState(0);

        ThrowUtil.ifTrue(!userRepo.insertUser(user), ErrorCode.SYSTEM_ERROR, "注册失败：服务器错误！");

        return user;
    }

    public Authorization getAuthorization() {
        Authorization authorization = new Authorization();
        authorization.setUserId(this.getId());
        authorization.setRoles(this.getRoles());
        return authorization;
    }

}
