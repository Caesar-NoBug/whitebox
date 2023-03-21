package com.caesar.controller;

import com.caesar.constant.enums.Code;
import com.caesar.mapper.UserMapper;
import com.caesar.model.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RegisterController {

    @PostMapping( "/register")
    public Response register(){
        String regType = "sdf";
        System.out.println(regType);
        switch (regType){
            case "wechat":
                return wechatRegister();
            case "qq":
                return qqRegister();
            case "email":
                return emailRegister();
        }
        //TODO: new response 会出现问题：406 not acceptable
        return new Response(Code.REQUEST_ERROR, null, "undefined register type");
    }

    private Response wechatRegister(){
        return null;
    }

    private Response qqRegister(){
        return null;
    }

    private Response emailRegister(){
        return null;
    }

}
