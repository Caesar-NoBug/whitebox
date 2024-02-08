package org.caesar.user.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.log.LogUtil;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.str.StrUtil;
import org.caesar.user.constant.CacheKey;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.enums.LogType;
import org.caesar.domain.user.vo.RoleVO;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.domain.user.enums.AuthMethod;
import org.caesar.user.auth.AuthenticationManager;
import org.caesar.user.model.MsMenuStruct;
import org.caesar.user.model.MsUserStruct;
import org.caesar.user.model.entity.User;
import org.caesar.domain.user.request.RegisterRequest;
import org.caesar.domain.user.request.LoginRequest;
import org.caesar.domain.user.vo.UserVO;
import org.caesar.user.repository.UserRepository;
import org.caesar.user.service.UserService;
import org.caesar.common.str.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author caesar
 * @description 针对表【sys_user_base】的数据库操作Service实现
 * @createDate 2023-05-01 09:36:22
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final int DEFAULT_REFRESH_TOKEN_LENGTH = 32;

    // refresh token默认过期时间：7天
    public static final int REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60;

    @Resource
    private UserRepository userRepo;

    @Resource
    private MsUserStruct userStruct;

    @Resource
    private MsMenuStruct menuStruct;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private AuthenticationManager authManager;

    @Override
    public UserVO login(LoginRequest request) {

        String identity = request.getIdentity();
        String credential = request.getCredential();
        AuthMethod method = request.getMethod();

        // 验证用户身份
        authManager.authenticate(method, identity, credential);

        //获取用户信息
        User user = authManager.getIdenticalUser(method, identity);

        ThrowUtil.ifNull(user, "fail to login: user does not exists.");

        long userId = user.getId();

        UserVO userVO = loadUserWithToken(user);

        LogUtil.info(LogType.LOGIN, "User login success: " + userId);

        return userVO;
    }

    @Override
    public String refreshToken(long userId, String refreshToken) {

        String cacheKey = CacheKey.AUTH_REFRESH_TOKEN + userId;

        String realRefreshToken = cacheRepo.getObject(cacheKey);

        ThrowUtil.ifTrue(Objects.isNull(realRefreshToken) || !realRefreshToken.equals(refreshToken),
                "refresh token expired or invalid refresh token");

        String newJwt = JwtUtil.createJWT(String.valueOf(userId));

        long expireTime = cacheRepo.getExpire(cacheKey);

        if (expireTime < REFRESH_TOKEN_EXPIRE / 2) {
            cacheRepo.expire(cacheKey, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
        }

        return newJwt;
    }

    @Override
    public UserVO register(RegisterRequest request) {

        AuthMethod method = request.getMethod();
        String identity = request.getIdentity();
        String credential = request.getCredential();

        // 验证用户身份
        authManager.authenticate(method, identity, credential);

        // 验证用户是否已存在
        User user = authManager.getIdenticalUser(method, identity);

        ThrowUtil.ifTrue(Objects.nonNull(user), "Fail to register: user already exists.");

        long userId = cacheRepo.nextId(CacheKey.USER_INC_ID);

        user = User.register(userId, request);

        ThrowUtil.ifTrue(!userRepo.insertUser(user), ErrorCode.SYSTEM_ERROR, "Fail to insert user into database.");

        return loadUserWithToken(user);
    }

    @Override
    public Map<Long, UserMinVO> getUserMin(List<Long> userIds) {
        // 没有被缓存的用户id
        ArrayList<Long> nonCachedIds = new ArrayList<>();

        Map<Long, UserMinVO> userMinMap = new HashMap<>();

        for (Long userId : userIds) {
            UserMinVO userMin = cacheRepo.cache(CacheKey.CACHE_USER_MIN + userId,
                    () -> null);

            if (Objects.nonNull(userMin)) userMinMap.put(userId, userMin);
            else nonCachedIds.add(userId);
        }

        if (!nonCachedIds.isEmpty()) {
            userRepo.selectUserMinByIds(nonCachedIds).forEach(user -> {
                Long id = user.getId();
                UserMinVO userMinVO = userStruct.DOtoMinVO(user);
                userMinMap.put(id, userMinVO);
                cacheRepo.cache(CacheKey.CACHE_USER_MIN + id, () -> userMinVO);
            });
        }

        return userMinMap;
    }

    @Override
    public List<RoleVO> getUpdatedRole(LocalDateTime updateTime) {
        return userRepo.getUpdatedRoles(updateTime)
                .stream()
                .map(menuStruct::roleDOtoVO)
                .collect(Collectors.toList());
    }

    private UserVO loadUserWithToken(User user) {

        long userId = user.getId();

        // 构建token和refresh token
        String authorization = JSON.toJSONString(user.getAuthorization());
        String token = JwtUtil.createJWT(authorization);
        String refreshToken = StrUtil.genRandStr(DEFAULT_REFRESH_TOKEN_LENGTH);
        cacheRepo.setObject(CacheKey.AUTH_REFRESH_TOKEN + userId, refreshToken, REFRESH_TOKEN_EXPIRE);

        // 封装token到userVO中
        UserVO userVO = userStruct.DOtoVO(user);
        userVO.setToken(token);
        userVO.setRefreshToken(refreshToken);

        return userVO;
    }

}




