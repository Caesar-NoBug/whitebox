package org.caesar.controller;


import org.caesar.common.Response;
import org.caesar.model.req.LoginRequest;
import org.caesar.model.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:8081",
        allowedHeaders = {"token", "Content-Type"},
        allowCredentials = "true"
)
@RestController
@RequestMapping("/user/login")
public class LoginController {

  /*  @Autowired
    private LoginService loginService;*/

    //TODO: 用策略模式加模板方法模式整合一下
    /*@PostMapping
    public Response<UserVO> login(LoginRequest request) {
        return ;
    }*/

    /*public Response loginUsername(@RequestBody DirectLoginRequest request){

        System.out.println("login visited");
        String username = request.getUsername();
        String password = request.getPassword();

        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Response.error(NumConstant.CODE_ILLEGAL_PARAM, "参数错误");
        }

        //TODO: map改成对象
        Map<String, Object> map = loginService.loginUsername(username, password);
        if (Objects.isNull(map))
            return Response.error("用户名或密码错误");

        return Res6ponse.ok(map, "登录成功");
    }*/

    /*@PostMapping("/third")
    public Response<UserVO> loginThird() {
        return null;
    }*/

    /*@RequestMapping("/email")
    public Response loginEmail(@RequestBody EmailLoginRequest request){
        String email = request.getEmail();
        String code = request.getCode();

        if (StrUtil.isBlank(email) || !StrUtil.isEmail(email) || StrUtil.isBlank(code)) {
            return Response.error(NumConstant.CODE_ILLEGAL_PARAM, "邮箱格式错误或验证码为空");
        }

        //TODO: map改成对象
        Map<String, Object> map = loginService.loginEmail(email, code);
        if (Objects.isNull(map))
            return Response.error("验证码失效或验证码过期");

        return Response.ok(map, "登录成功");
    }

    @RequestMapping("/phone")
    public Response loginPhone(@RequestBody UserPO user){
        return null;
    }*/

}
