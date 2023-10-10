package org.caesar.controller;

import org.caesar.model.dto.TokenDTO;
import org.caesar.model.entity.BaseUser;
import org.caesar.model.vo.Response;
import org.caesar.service.UserService;
import org.caesar.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@CrossOrigin(
        origins = "http://localhost:8081",
        allowedHeaders = {"token", "Content-Type"},
        allowCredentials = "true"
)
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/authorize")
    public Response<String> authorize(@RequestParam String jwt, @RequestParam String requestPath) {

        System.out.println("认证接口被访问了");
        if (StrUtil.isBlank(jwt) || StrUtil.isBlank(requestPath)) {
            return Response.error("jwt或请求路径不合法");
        }

        return userService.authorize(jwt, requestPath);
    }

    @PostMapping("/refreshToken")
    public Response refreshToken(@RequestBody TokenDTO tokenDTO) {
        String refreshToken = tokenDTO.getRefreshToken();
        Long userId = tokenDTO.getUserId();
        LocalDateTime lastUpdateTime = tokenDTO.getLastUpdateTime();

        if (StrUtil.isBlank(refreshToken) || Objects.isNull(lastUpdateTime))
            return Response.error("非法的请求参数，请重试");

        return userService.refreshToken(userId, refreshToken, lastUpdateTime);
    }

    //TODO:优化用户模块请求，设计定制的请求类
    @PostMapping("/register")
    public Response register(@RequestBody BaseUser baseUser) {

        if (StrUtil.isBlank(baseUser.getEmail()) || StrUtil.isBlank(baseUser.getUsername()) || StrUtil.isBlank(baseUser.getPassword()))
            return Response.error("注册信息不完善，请重新填写！");

        return userService.register(baseUser);
    }

    @DeleteMapping("/logout")
    public Response logout(@RequestBody TokenDTO tokenDTO) {
        if (StrUtil.isBlank(tokenDTO.getJwt()))
            return Response.error("非法token，请重试");
        return userService.logout(tokenDTO.getJwt());
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
