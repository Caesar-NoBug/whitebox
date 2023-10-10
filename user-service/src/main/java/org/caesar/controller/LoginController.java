package org.caesar.controller;


import org.caesar.common.constant.NumConstant;
import org.caesar.model.entity.BaseUser;
import org.caesar.model.vo.Response;
import org.caesar.service.LoginService;
import org.caesar.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@CrossOrigin(
        origins = "http://localhost:8081",
        allowedHeaders = {"token", "Content-Type"},
        allowCredentials = "true"
)
@RestController
@RequestMapping("/user/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/username")
    public Response loginUsername(@RequestBody BaseUser user){

        System.out.println("login visited");
        String username = user.getUsername();
        String password = user.getPassword();

        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Response.error(NumConstant.CODE_ILLEGAL_PARAM, "参数错误");
        }

        Map<String, Object> map = loginService.loginUsername(username, password);
        if (Objects.isNull(map))
            return Response.error("用户名或密码错误");

        return Response.ok(map, "登录成功");
    }

    @RequestMapping("/email")
    public Response loginEmail(@RequestBody BaseUser user){
        String email = user.getEmail();
        String code = user.getCode();

        if (StrUtil.isBlank(email) || !StrUtil.isEmail(email) || StrUtil.isBlank(code)) {
            return Response.error(NumConstant.CODE_ILLEGAL_PARAM, "邮箱格式错误或验证码为空");
        }

        Map<String, Object> map = loginService.loginEmail(email, code);
        if (Objects.isNull(map))
            return Response.error("验证码失效或验证码过期");

        return Response.ok(map, "登录成功");
    }

    @RequestMapping("/phone")
    public Response loginPhone(@RequestBody BaseUser user){
        return null;
    }

}
