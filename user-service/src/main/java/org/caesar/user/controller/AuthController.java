package org.caesar.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.caesar.common.log.Logger;
import org.caesar.user.captcha.vo.Captcha;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.user.request.CaptchaRequest;
import org.caesar.user.model.dto.TokenDTO;
import org.caesar.domain.user.request.LoginRequest;
import org.caesar.domain.user.request.RegisterRequest;
import org.caesar.domain.user.vo.UserVO;
import org.caesar.user.service.CaptchaService;
import org.caesar.user.service.UserService;
import org.caesar.common.str.StrUtil;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Min;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@Validated
@Api(tags = "用户认证服务")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private CaptchaService captchaService;

    // 认证次数
    public static final String SESSION_AUTHENTICATION_TIME = "Authentication_time";

    @ApiOperation("获取用户自己的基本信息")
    @Logger(value = "getLoginUser", args = true)
    @GetMapping("/login-user")
    public Response<UserVO> getLoginUser(@RequestParam Long userId, @RequestParam String refreshToken) {
        return Response.ok(userService.getLoginUser(userId, refreshToken));
    }

    @ApiOperation("用户登录")
    @Logger(value = "login", args = true)
    @PostMapping("/login")
    public Response<UserVO> login(@RequestBody LoginRequest request) {

        ThrowUtil.ifFalse(captchaService.validated(request.getCaptchaId()), ErrorCode.NOT_AUTHENTICATED_ERROR,
                "Captcha failed, please pass the captcha first.");

        return Response.ok(userService.login(request));
    }

    /*@ApiOperation("刷新token")
    @PostMapping("/refresh-token")
    public Response<String> refreshToken(@RequestBody TokenDTO tokenDTO) {
        String refreshToken = tokenDTO.getRefreshToken();
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(userService.refreshToken(userId, refreshToken));
    }*/

    //TODO: 对邮箱做更严格的格式校验和处理，防止恶意邮箱注册
    //TODO: 要求用户密码为一个较为复杂的格式
    @ApiOperation("注册")
    @Logger(value = "register", args = true)
    @PostMapping("/register")
    public Response<UserVO> register(@Validated @RequestBody RegisterRequest request) {

        ThrowUtil.ifFalse(captchaService.validated(request.getCaptchaId()), ErrorCode.NOT_AUTHENTICATED_ERROR,
                "Captcha failed, please pass the captcha first.");

        if (StrUtil.isBlank(request.getIdentity()) || StrUtil.isBlank(request.getUsername()) || StrUtil.isBlank(request.getPassword()))
            return Response.error("Incomplete register information, please fill in complete information");

        return Response.ok(userService.register(request));
    }

    @ApiOperation("生成验证码")
    @Logger(value = "/genCaptcha")
    @GetMapping("/gen-captcha")
    public Response<Captcha> genCaptcha(@Min(20) @RequestParam Integer width,
                                        @Min(20) @RequestParam Integer height) {
        return Response.ok(captchaService.refreshCaptcha(width, height));
    }

    @ApiOperation("人机校验")
    @Logger(value = "/captcha")
    @PostMapping("captcha")
    public Response<Void> captcha(@RequestBody CaptchaRequest request) {
        captchaService.validate(request.getCaptchaId(), request.getAnswer());
        return Response.ok();
    }

    @ApiOperation("测试用户token是否可用")
    @Logger(value = "/test-token")
    @GetMapping("/test-token")
    public Response<Void> testToken() {
        return Response.ok();
    }

    @PostMapping("/reset")
    public Response<Void> reset() {
        return null;
    }
}
