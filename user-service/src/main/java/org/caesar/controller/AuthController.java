package org.caesar.controller;

import org.caesar.domain.common.vo.Response;
import org.caesar.model.dto.TokenDTO;
import org.caesar.model.req.LoginRequest;
import org.caesar.model.req.RegisterRequest;
import org.caesar.model.vo.UserVO;
import org.caesar.service.UserService;
import org.caesar.common.str.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

/*@CrossOrigin(
        origins = "http://localhost:8081",
        allowedHeaders = {"token", "Content-Type"},
        allowCredentials = "true"
)*/
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Response<UserVO> login(@RequestBody LoginRequest request) {
        return Response.ok(userService.login(request));
    }

    //TODO: 将权限相关信息根据角色进行缓存,将角色信息存储至jwt中，把这些操作移动至gateway中，并想清楚同步机制
    //TODO: 删除所有User模块中的跨域设置
    //TODO: 缓存一下非法的token避免DDos
    @GetMapping("/authorize")
    public Response<Long> authorize(@RequestParam String jwt, @RequestParam String requestPath) {

        if (StrUtil.isBlank(jwt) || StrUtil.isBlank(requestPath)) {
            return Response.error("illegal token or request uri");
        }
        //TODO: 加上请求成功的信息
        return Response.ok(userService.authorize(jwt, requestPath));
    }

    @PostMapping("/refreshToken")
    public Response refreshToken(@RequestBody TokenDTO tokenDTO) {
        String refreshToken = tokenDTO.getRefreshToken();
        Long userId = tokenDTO.getUserId();
        LocalDateTime lastUpdateTime = tokenDTO.getLastUpdateTime();

        if (StrUtil.isBlank(refreshToken) || Objects.isNull(lastUpdateTime))
            return Response.error("非法的请求参数，请重试");

        return Response.ok(userService.refreshToken(userId, refreshToken, lastUpdateTime));
    }

    //TODO: 注册也用登录的方式整合一下
    //TODO: 加一个人机校验(captcha)
    //TODO: 对邮箱做更严格的格式校验和处理，防止恶意邮箱注册
    //TODO: 要求用户密码为一个较为复杂的格式
    @PostMapping("/register")
    public Response register(@RequestBody RegisterRequest request) {

        if (StrUtil.isBlank(request.getIdentity()) || StrUtil.isBlank(request.getUsername()) || StrUtil.isBlank(request.getPassword()))
            return Response.error("注册信息不完善，请重新填写！");

        return Response.ok(userService.register(request));
    }

    @DeleteMapping("/logout")
    public Response<Void> logout(@RequestBody TokenDTO tokenDTO) {

        if (StrUtil.isBlank(tokenDTO.getJwt()))
            return Response.error("非法token，请重试");

        userService.logout(tokenDTO.getJwt());

        return Response.ok(null);
    }

    @RequestMapping("/reset/phone")
    public Response resetByPhone() {
        return null;
    }

    @RequestMapping("/reset/email")
    public Response resetByEmail() {
        return null;
    }
}
