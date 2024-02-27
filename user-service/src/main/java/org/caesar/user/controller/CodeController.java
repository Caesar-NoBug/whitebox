package org.caesar.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.caesar.domain.common.vo.Response;
import org.caesar.user.captcha.vo.Captcha;
import org.caesar.user.service.CodeService;
import org.caesar.common.str.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user/sendCode")
@Api("验证码服务")
public class CodeController {

    @Resource
    private CodeService codeService;

    @ApiOperation("发送邮箱登录验证码")
    @PostMapping("/login/email/{email}")
    public Response<Void> sendLoginEmailCode(HttpServletRequest httpServletRequest, @PathVariable String email){

        refreshAuthenticationTime(httpServletRequest);

        if(StrUtil.checkString(email, 100) || !StrUtil.isEmail(email))
            return Response.error("邮箱格式错误");

        codeService.sendLoginEmailCode(email);

        return Response.ok(null, "验证码已成功发送至邮箱，请查看");
    }

    @ApiOperation("发送邮箱注册验证码")
    @PostMapping("/register/email/{email}")
    public Response<Void> sendRegisterEmailCode(HttpServletRequest httpServletRequest, @PathVariable String email){

        refreshAuthenticationTime(httpServletRequest);

        if(StrUtil.isBlank(email) || !StrUtil.isEmail(email))
            return Response.error("邮箱格式错误");

        codeService.sendRegisterEmailCode(email);

        return Response.ok(null, "验证码已成功发送至邮箱，请查看");
    }

    @PostMapping("/captcha")
    public Response<Captcha> genCaptcha() {
        return null;
    }

    @PostMapping("/login/phone/{phone}")
    public Response<Void> sendLoginPhoneCode(@PathVariable String phone){
        return null;
    }

    @PostMapping("/reset/email/{email}")
    public Response<Void> sendResetEmailCode(@PathVariable String email){
        return null;
    }

    @PostMapping("/reset/phone/{phone}")
    public Response<Void> sendResetPhoneCode(@PathVariable String phone){
        return null;
    }

    private void refreshAuthenticationTime(HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute(AuthController.SESSION_AUTHENTICATION_TIME, 0);
    }

}
