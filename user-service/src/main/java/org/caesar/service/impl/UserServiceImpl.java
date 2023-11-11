package org.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.caesar.common.Response;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.constant.NumConstant;
import org.caesar.constant.RedisPrefix;
import org.caesar.enums.AuthenticationMethod;
import org.caesar.manager.AuthenticationManager;
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
import org.caesar.common.util.JwtUtil;
import org.caesar.common.util.PrefixMatcher;
import org.caesar.common.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserServiceImpl extends ServiceImpl<BaseUserMapper, UserPO> implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Resource
    private MsUserStruct userStruct;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AuthenticationManager authManager;

    private final Map<Integer, PrefixMatcher> authorizeMap = new ConcurrentHashMap<>();

    @Override
    public UserVO login(LoginRequest request) {

        String identity = request.getIdentity();
        String credential = request.getCredential();
        AuthenticationMethod method = request.getMethod();

        authManager.authenticate(method, identity, credential);

        User user = authManager.getIdenticalUser(method, identity);

        ThrowUtil.throwIfNull(user, "登录失败：该用户不存在！");

        return userStruct.DOtoVO(user);
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

        String realJwt = redisCache.getCacheObject(RedisPrefix.LOGIN_JWT + userId);

        //jwt已失效
        ThrowUtil.throwIf(!jwt.equals(realJwt), NumConstant.CODE_NOT_AUTHENTICATED, "jwt已失效，请重新登录");

        UserDTO user = redisCache.getCacheObject(RedisPrefix.LOGIN_USER + userId);

        List<Integer> roles = user.getRoles();

        boolean accepted = false;
        for (Integer role : roles) {
            PrefixMatcher authorizeMatcher = getAuthorizeMatcher(role);
            accepted |= authorizeMatcher.match(requestPath);
        }

        ThrowUtil.throwIf(!accepted, NumConstant.CODE_NOT_AUTHORIZED, "用户无权限访问该接口");

        return userId;
    }

    @Override
    public String refreshToken(long userId, String refreshToken, LocalDateTime lastUpdateTime) {

        UserDTO userDTO = redisCache.getCacheObject(RedisPrefix.LOGIN_USER + userId);

        ThrowUtil.throwIf(Objects.isNull(userDTO) || !refreshToken.equals(userDTO.getRefreshToken()), "refresh token已失效，请重新登录");

        String newJwt = JwtUtil.createJWT(userId + "");
        redisCache.setCacheObject(RedisPrefix.LOGIN_JWT + userId, newJwt, 1, TimeUnit.HOURS);

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setJwt(newJwt);
        tokenDTO.setRefreshToken(null);

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", tokenDTO);

        if (!lastUpdateTime.equals(userDTO.getUserPO().getUpdateTime()))
            map.put("user", userDTO.getUserPO());

        return Response.ok(map);
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

        redisCache.deleteObject(redisKey);

        return Response.ok(null, "注册成功，请登录");
    }

    @Override
    public void logout(String jwt) {

        String userId = JwtUtil.getJwtSubject(jwt);
        if (Objects.isNull(userId)) {
            return Response.error("退出失败，用户不存在");
        }
        redisCache.deleteObject(RedisPrefix.LOGIN_USER + userId);
        redisCache.deleteObject(RedisPrefix.LOGIN_JWT + userId);
        return Response.ok(null, "成功退出登录");
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
}




