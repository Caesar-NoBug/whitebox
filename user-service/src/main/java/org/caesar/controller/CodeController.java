package org.caesar.controller;

import org.caesar.common.vo.Response;
import org.caesar.common.captcha.vo.Captcha;
import org.caesar.service.CodeService;
import org.caesar.common.str.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/user/sendCode")
public class CodeController {

    //TODO: 登录验证码相关加一个人机验证，把这个服务移动到authController中
    @Autowired
    private CodeService codeService;

    @PostMapping("/login/email/{email}")
    public Response<Void> sendLoginEmailCode(@PathVariable String email){

        if(StrUtil.checkString(email, 100) || !StrUtil.isEmail(email))
            return Response.error("邮箱格式错误");

        codeService.sendLoginEmailCode(email);

        return Response.ok(null, "验证码已成功发送至邮箱，请查看");
    }

    @PostMapping("/register/email/{email}")
    public Response<Void> sendRegisterEmailCode(@PathVariable String email){

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

}
