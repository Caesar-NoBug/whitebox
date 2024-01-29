package org.caesar.user.controller;

import org.caesar.common.captcha.vo.Captcha;
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
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private CaptchaService captchaService;

    // 认证次数
    public static final String SESSION_AUTHENTICATION_TIME = "Authentication_time";
    // 认证次数限制
    public static final int AUTHENTICATION_RETRY_TIME = 5;

    @PostMapping("/login")
    public Response<UserVO> login(HttpServletRequest httpServletRequest,
                                  @Validated @RequestBody LoginRequest request) {

        ThrowUtil.ifFalse(captchaPassed(httpServletRequest), ErrorCode.NOT_AUTHENTICATED_ERROR,
                "Captcha failed, please pass the captcha first.");

        return Response.ok(userService.login(request));
    }

    @PostMapping("/refresh-token")
    public Response<String> refreshToken(@RequestBody TokenDTO tokenDTO) {
        String refreshToken = tokenDTO.getRefreshToken();
        Long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(userService.refreshToken(userId, refreshToken));
    }

    //TODO: 对邮箱做更严格的格式校验和处理，防止恶意邮箱注册
    //TODO: 要求用户密码为一个较为复杂的格式
    @PostMapping("/register")
    public Response<UserVO> register(HttpServletRequest httpServletRequest,
                                     @Validated @RequestBody RegisterRequest request) {

        ThrowUtil.ifFalse(captchaPassed(httpServletRequest), ErrorCode.NOT_AUTHENTICATED_ERROR,
                "Captcha failed, please pass the captcha first.");

        if (StrUtil.isBlank(request.getIdentity()) || StrUtil.isBlank(request.getUsername()) || StrUtil.isBlank(request.getPassword()))
            return Response.error("Incomplete register information, please fill in complete information");

        return Response.ok(userService.register(request));
    }

    @GetMapping("/gen-captcha")
    public Response<Captcha> genCaptcha(HttpServletRequest httpServletRequest,
                                        @Min(20) @RequestParam Integer width,
                                        @Min(20) @RequestParam Integer height) {
        return Response.ok(captchaService.refreshCaptcha());
    }

    @PostMapping("captcha")
    public Response<Void> captcha(HttpServletRequest httpServletRequest,
                                  @RequestBody CaptchaRequest request) {
    }

    @RequestMapping("/reset")
    public Response<Void> reset() {
        return null;
    }

    private boolean captchaPassed(HttpServletRequest httpServletRequest) {
        Object result = httpServletRequest.getSession().getAttribute(SESSION_CAPTCHA_RESULT);

        return Objects.nonNull(result) && (Integer) result == 1;
    }

    private void checkAuthenticationTime(HttpServletRequest httpServletRequest) {
        Object authTime = httpServletRequest.getSession().getAttribute(SESSION_AUTHENTICATION_TIME);
        ThrowUtil.ifTrue((Integer)authTime > AUTHENTICATION_RETRY_TIME,
                ErrorCode.NOT_AUTHENTICATED_ERROR, "Too much retry times, please refresh the code.");
    }

    private void addAuthenticationTime(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        Object authTime = session.getAttribute(SESSION_AUTHENTICATION_TIME);
        if (Objects.isNull(authTime)) session.setAttribute(SESSION_AUTHENTICATION_TIME, 0);
        else session.setAttribute(SESSION_AUTHENTICATION_TIME, (Integer) authTime + 1);
    }

}