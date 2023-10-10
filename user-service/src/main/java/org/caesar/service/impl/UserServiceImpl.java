package org.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import org.caesar.common.constant.NumConstant;
import org.caesar.constant.RedisPrefix;
import org.caesar.common.constant.StrConstant;
import org.caesar.mapper.BaseUserMapper;
import org.caesar.mapper.MenuMapper;
import org.caesar.model.dto.AuthUser;
import org.caesar.model.dto.TokenDTO;
import org.caesar.model.entity.BaseUser;
import org.caesar.model.vo.Response;
import org.caesar.service.UserService;
import org.caesar.common.util.JwtUtil;
import org.caesar.common.util.PrefixMatcher;
import org.caesar.common.util.RedisCache;
import org.caesar.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author caesar
 * @description 针对表【sys_user_base】的数据库操作Service实现
 * @createDate 2023-05-01 09:36:22
 */
@Service
public class UserServiceImpl extends ServiceImpl<BaseUserMapper, BaseUser> implements UserService {

    @Autowired
    private BaseUserMapper baseUserMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Map<Integer, PrefixMatcher> authorizeMap = new ConcurrentHashMap<>();

    @Override
    public BaseUser selectBaseUserById(Long id) {
        return baseUserMapper.selectById(id);
    }

    @Override
    //根据用户id查询带权限信息的用户信息
    public AuthUser selectAuthUserById(Long id) {
        BaseUser baseUser = baseUserMapper.selectById(id);
        return loadUserWithPermissions(baseUser);
    }

    @Override
    //根据用户名查询带权限信息的用户信息
    public AuthUser selectAuthUserByUsername(String username) {
        BaseUser baseUser = baseUserMapper.selectByUsername(username);
        return loadUserWithPermissions(baseUser);
    }

    @Override
    //根据用户邮箱查询带权限信息的用户信息
    public AuthUser selectAuthUserByEmail(String email) {
        BaseUser baseUser = baseUserMapper.selectByEmail(email);
        return loadUserWithPermissions(baseUser);
    }

    @Override
    //根据用户手机号查询带权限信息的用户信息
    public AuthUser selectAuthUserByPhone(String phone) {
        BaseUser baseUser = baseUserMapper.selectByPhone(phone);
        return loadUserWithPermissions(baseUser);
    }

    //TODO: 优化认证逻辑：通过角色类型来判断允许访问路径，并缓存允许访问的路径，并使用前缀匹配器来匹配
    @Override
    public Response<String> authorize(String jwt, String requestPath) {

        String userId = null;

        try {
            Claims claims = JwtUtil.parseJWT(jwt);
            userId = claims.getSubject();
        } catch (Exception e) {
            //jwt不合法
            return new Response<>(NumConstant.CODE_NOT_AUTHENTICATED, null, "非法jwt，请重新登录");
        }

        String realJwt = redisCache.getCacheObject(RedisPrefix.LOGIN_JWT + userId);

        //jwt已失效
        if (!jwt.equals(realJwt)) {
            return new Response<>(NumConstant.CODE_NOT_AUTHENTICATED, null, "jwt已失效，请重新登录");
        }

        AuthUser user = redisCache.getCacheObject(RedisPrefix.LOGIN_USER + userId);

        List<Integer> roles = user.getRoles();

        boolean accepted = false;
        for (Integer role : roles) {
            PrefixMatcher authorizeMatcher = getAuthorizeMatcher(role);
            accepted |= authorizeMatcher.match(requestPath);
        }

        if(accepted)
            return Response.ok(userId, "允许用户访问该接口");

        return new Response<>(NumConstant.CODE_NOT_AUTHORIZED, null, "用户无权限访问该接口");
    }

    private PrefixMatcher getAuthorizeMatcher(int roleId) {
        PrefixMatcher authorizeMatcher = authorizeMap.get(roleId);

        if (Objects.isNull(authorizeMatcher)) {
            List<String> permissions = menuMapper.selectPermsByRoleId(roleId);
            authorizeMatcher = new PrefixMatcher(permissions);
            authorizeMap.put(roleId, authorizeMatcher);
        }

        return authorizeMatcher;
    }

    @Override
    public Response refreshToken(long userId, String refreshToken, LocalDateTime lastUpdateTime) {

        AuthUser authUser = redisCache.getCacheObject(RedisPrefix.LOGIN_USER + userId);

        if (Objects.isNull(authUser) || !refreshToken.equals(authUser.getRefreshToken()))
            return Response.error("refresh token已失效，请重新登录");

        String newJwt = JwtUtil.createJWT(userId + "");
        redisCache.setCacheObject(RedisPrefix.LOGIN_JWT + userId, newJwt, 1, TimeUnit.HOURS);

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setJwt(newJwt);
        tokenDTO.setRefreshToken(null);

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", tokenDTO);

        if (!lastUpdateTime.equals(authUser.getBaseUser().getUpdateTime()))
            map.put("user", authUser.getBaseUser());

        return Response.ok(map);
    }

    @Override
    public Response register(BaseUser baseUser) {
        int count = baseUserMapper.selectSimilarUserCount(baseUser);
        if (count > 0)
            return Response.error("注册失败：已存在相同用户名或邮箱");

        String redisKey = RedisPrefix.REGISTER_CODE_EMAIL + baseUser.getEmail();
        String registerCode = redisCache.getCacheObject(redisKey);

        if (StrUtil.isBlank(registerCode))
            return Response.error("注册失败：验证码已失效，请重新发送验证码");

        if (!registerCode.equals(baseUser.getCode()))
            return Response.error("注册失败：验证码错误，请重新输入验证码");

        String password = passwordEncoder.encode(baseUser.getPassword());

        baseUser.setPassword(password);
        baseUser.setId(redisCache.nextId(RedisPrefix.USER_INC_ID));
        //TODO: 随机设置默认头像
        baseUser.setAvatarUrl(StrConstant.DEFAULT_AVATAR_URL);
        LocalDateTime now = LocalDateTime.now();
        baseUser.setCreateTime(now);
        baseUser.setUpdateTime(now);
        baseUser.setState(0);

        if (baseUserMapper.insertUser(baseUser) <= 0)
            return new Response(NumConstant.CODE_INNER_ERROR, null, "注册失败：服务器错误！");

        redisCache.deleteObject(redisKey);

        return Response.ok(null, "注册成功，请登录");
    }

    @Override
    public Response logout(String jwt) {

        String userId = JwtUtil.getJwtSubject(jwt);
        if (Objects.isNull(userId)) {
            return Response.error("退出失败，用户不存在");
        }
        redisCache.deleteObject(RedisPrefix.LOGIN_USER + userId);
        redisCache.deleteObject(RedisPrefix.LOGIN_JWT + userId);
        return Response.ok(null, "成功退出登录");
    }

    //封装权限信息
    private AuthUser loadUserWithPermissions(BaseUser baseUser) {
        if (Objects.isNull(baseUser)) {
            return null;
        }

        long userId = baseUser.getId();
        List<String> permissions = menuMapper.selectPermsByUserId(userId);
        List<Integer> roles = menuMapper.selectRolesByUserId(userId);

        return new AuthUser(baseUser, permissions, roles);
    }

}




