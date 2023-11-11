package org.caesar.controller;

import org.caesar.common.Response;
import org.caesar.service.CodeService;
import org.caesar.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:8081",
        allowedHeaders = {"token", "Content-Type"},
        allowCredentials = "true"
)
@RestController
@RequestMapping("/user/sendCode")
public class CodeController {

    //TODO: 登录验证码相关加一个人机验证
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
