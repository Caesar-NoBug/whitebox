package org.caesar.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.repository.CacheRepository;
import org.caesar.common.str.StrUtil;
import org.caesar.domain.constant.NumConstant;
import org.caesar.constant.RedisPrefix;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.enums.AuthenticationMethod;
import org.caesar.auth.AuthenticationManager;
import org.caesar.mapper.BaseUserMapper;
import org.caesar.mapper.MenuMapper;
import org.caesar.model.MsUserStruct;
import org.caesar.model.dto.TokenDTO;
import org.caesar.model.dto.UserDTO;
import org.caesar.model.entity.User;
import org.caesar.model.req.RegisterRequest;
import org.caesar.model.po.UserPO;
import org.caesar.model.req.LoginRequest;
import org.caesar.model.vo.UserVO;
import org.caesar.repository.UserRepository;
import org.caesar.service.UserService;
import org.caesar.common.str.JwtUtil;
import org.caesar.common.str.PrefixMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author caesar
 * @description 针对表【sys_user_base】的数据库操作Service实现
 * @createDate 2023-05-01 09:36:22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<BaseUserMapper, UserPO> implements UserService {

    public static final int DEFAULT_REFRESH_TOKEN_LENGTH = 32;

    @Autowired
    private UserRepository userRepo;

    @Resource
    private MsUserStruct userStruct;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private CacheRepository cacheRepo;

    @Autowired
    private AuthenticationManager authManager;

    private final Map<Integer, PrefixMatcher> authorizeMap = new ConcurrentHashMap<>();

    @Override
    public UserVO login(LoginRequest request) {

        String identity = request.getIdentity();
        String credential = request.getCredential();
        AuthenticationMethod method = request.getMethod();

        // 验证用户身份
        authManager.authenticate(method, identity, credential);

        //获取用户信息
        User user = authManager.getIdenticalUser(method, identity);

        ThrowUtil.ifNull(user, "登录失败：该用户不存在！");

        long userId = user.getId();
        String authorization = JSON.toJSONString(user.getAuthorization());

        String token = JwtUtil.createJWT(authorization);
        String refreshToken = StrUtil.genRandStr(DEFAULT_REFRESH_TOKEN_LENGTH);

        UserVO userVO = userStruct.DOtoVO(user);

        userVO.setToken(token);
        userVO.setRefreshToken(refreshToken);

        cacheRepo.setObject(RedisPrefix.AUTH_REFRESH_TOKEN + userId, refreshToken, 7, TimeUnit.DAYS);
        //redisCache.setCacheObject(RedisPrefix.LOGIN_USER + userId, authUser, 30, TimeUnit.DAYS);
        //redisCache.setCacheObject(RedisPrefix.LOGIN_JWT + userId, token, 1, TimeUnit.HOURS);
        log.info("用户登录成功：" + userId);

        return userVO;
    }

    @Override
    public Long authorize(String jwt, String requestPath) {

        long userId;

        try {
            Claims claims = JwtUtil.parseJWT(jwt);
            userId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            //jwt不合法
            throw new BusinessException(NumConstant.CODE_NOT_AUTHENTICATED, "非法jwt，请重新登录");
        }

        String realJwt = cacheRepo.getObject(RedisPrefix.AUTH_REFRESH_TOKEN + userId);

        //jwt已失效
        ThrowUtil.ifTrue(!jwt.equals(realJwt), ErrorCode.NOT_AUTHENTICATED_ERROR, "jwt已失效，请重新登录");

        UserDTO user = cacheRepo.getObject(RedisPrefix.LOGIN_USER + userId);

        List<Integer> roles = user.getRoles();

        boolean accepted = false;
        for (Integer role : roles) {
            PrefixMatcher authorizeMatcher = getAuthorizeMatcher(role);
            accepted |= authorizeMatcher.match(requestPath);
        }

        ThrowUtil.ifTrue(!accepted, ErrorCode.NOT_AUTHORIZED_ERROR, "用户无权限访问该接口");

        return userId;
    }

    @Override
    public String refreshToken(long userId, String refreshToken, LocalDateTime lastUpdateTime) {

        UserDTO userDTO = cacheRepo.getObject(RedisPrefix.LOGIN_USER + userId);

        ThrowUtil.ifTrue(Objects.isNull(userDTO) || !refreshToken.equals(userDTO.getRefreshToken()), "refresh token已失效，请重新登录");

        String newJwt = JwtUtil.createJWT(userId + "");
        cacheRepo.setObject(RedisPrefix.AUTH_REFRESH_TOKEN + userId, newJwt, 1, TimeUnit.HOURS);

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setJwt(newJwt);
        tokenDTO.setRefreshToken(null);

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", tokenDTO);

        if (!lastUpdateTime.equals(userDTO.getUserPO().getUpdateTime()))
            map.put("user", userDTO.getUserPO());

        return null;
    }

    @Override
    public UserVO register(RegisterRequest request) {
        /*User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        boolean isContains = userRepository.containsSimilarBindUser(user);

        if (isContains)
            return Response.error("注册失败：已存在相同用户名或邮箱");

        String redisKey = RedisPrefix.REGISTER_CODE_EMAIL + request.getEmail();
        String registerCode = redisCache.getCacheObject(redisKey);

        if (StrUtil.isBlank(registerCode))
            return Response.error("注册失败：验证码已失效，请重新发送验证码");

        if (!registerCode.equals(request.getCode()))
            return Response.error("注册失败：验证码错误，请重新输入验证码");

        String password = passwordEncoder.encode(request.getPassword());

        user.setPassword(password);
        user.setId(redisCache.nextId(RedisPrefix.USER_INC_ID));
        //TODO: 随机设置默认头像
        user.setAvatarUrl(StrConstant.DEFAULT_AVATAR_URL);
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setState(0);

        if (!userRepository.insertUser(user))
            return new Response<>(NumConstant.CODE_INNER_ERROR, null, "注册失败：服务器错误！");*/

        /*cacheRepo.deleteObject(redisKey);

        return Response.ok(null, "注册成功，请登录");*/
        return null;
    }

    @Override
    public void logout(String token) {

        String userId = null;
        try {
            userId = JwtUtil.getJwtSubject(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ILLEGAL_PARAM_ERROR, "退出登录失败：非法token");
        }

        ThrowUtil.ifNull(userId, "退出失败，用户不存在");

        cacheRepo.deleteObject(RedisPrefix.LOGIN_USER + userId);
        cacheRepo.deleteObject(RedisPrefix.AUTH_REFRESH_TOKEN + userId);

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
    public Map<Long, UserMinVO> getUserMin(List<Long> userIds) {
        ArrayList<Long> ids = new ArrayList<>();

        Map<Long, UserMinVO> userMinMap = new HashMap<>();

        for (Long userId : userIds) {
            UserMinVO userMinVO = cacheRepo.getObject(RedisPrefix.CACHE_USER_MIN + userId);
            if(userMinVO != null) userMinMap.put(userId, userMinVO);
            else ids.add(userId);
        }

        userRepo.selectUserByIds(userIds).forEach(user -> {
            Long id = user.getId();
            UserMinVO userMinVO = userStruct.DOtoMinVO(user);
            userMinMap.put(id, userMinVO);
            cacheRepo.setObject(RedisPrefix.CACHE_USER_MIN + id, userMinVO,
                    5, TimeUnit.MINUTES);
        });

        return userMinMap;
    }

}




