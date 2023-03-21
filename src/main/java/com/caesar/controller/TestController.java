package com.caesar.controller;

import com.caesar.mapper.UserMapper;
import com.caesar.model.entity.User;
import com.caesar.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/redis")
    public String testForRedis(){
        return redisUtil.getValue();
    }

    @PostMapping("/mybatis")
    public User testForMybatis(){
        return userMapper.getUserById(0);
    }

    @GetMapping("/controller")
    public String testForController(){
        return "controller is fine";
    }
}
