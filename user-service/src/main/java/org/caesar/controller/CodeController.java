package org.caesar.controller;

import org.caesar.model.entity.BaseUser;
import org.caesar.model.vo.Response;
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

    @Autowired
    private CodeService codeService;

    @PostMapping("/login/email/{email}")
    public Response sendLoginEmailCode(@PathVariable String email){

        if(StrUtil.checkString(email, 100) || !StrUtil.isEmail(email))
            return Response.error("邮箱格式错误");

        return codeService.sendLoginEmailCode(email);
    }

    @PostMapping("/register/email")
    public Response sendRegisterEmailCode(@RequestBody BaseUser user){
        String email = user.getEmail();
        if(StrUtil.isBlank(email) || !StrUtil.isEmail(email))
            return Response.error("邮箱格式错误");

        return codeService.sendRegisterEmailCode(email);
    }

    @PostMapping("/login/phone")
    public Response sendLoginPhoneCode(@RequestBody BaseUser user){
        return null;
    }

    @PostMapping("/reset/email")
    public Response sendResetEmailCode(@RequestBody BaseUser user){
        return null;
    }

    @PostMapping("/reset/phone")
    public Response sendResetPhoneCode(@RequestBody BaseUser user){
        return null;
    }

}
