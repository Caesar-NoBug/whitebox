package org.caesar.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.caesar.common.context.ContextHolder;
import org.caesar.common.idempotent.Idempotent;
import org.caesar.common.log.Logger;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.user.vo.RoleVO;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.domain.user.vo.UserPreferVO;
import org.caesar.domain.user.vo.UserVO;
import org.caesar.user.service.UserExtraService;
import org.caesar.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "用户服务")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserExtraService userExtraService;

    @ApiOperation("获取用户详情")
    @GetMapping("/me/detail")
    public Response<UserVO> getUserDetail() {
        //TODO: 实现这个功能
        return Response.ok(null);
    }

    @ApiOperation("获取用户基本信息")
    @Logger(value = "/getUserMin")
    @GetMapping("/min")
    public Response<Map<Long, UserMinVO>> getUserMin(@RequestParam List<Long> id) {
        return Response.ok(userService.getUserMin(id));
    }

    @ApiOperation("获取用户偏好信息")
    @GetMapping("/prefer")
    public Response<UserPreferVO> getUserPrefer() {
        long userId = ContextHolder.getUserIdNecessarily();
        return Response.ok(userExtraService.getUserPrefer(userId));
    }

    @ApiOperation("获取修改过的权限信息")
    @GetMapping("/updated-role")
    public Response<List<RoleVO>> getUpdatedRole(@RequestParam LocalDateTime updateTime) {
        return Response.ok(userService.getUpdatedRole(updateTime));
    }

    /*@ApiOperation("获取修改过的权限信息")
    @Idempotent(value = "testId", reqId = "#reqId", successMsg = "芜湖")
    @GetMapping("/testId")
    public Response<String> testId(@RequestParam Long reqId) {
        //int x = 1 / 0;
        return Response.ok("hahaha");
    }

    @GetMapping("/test-circuit-breaker")
    public Response<String> testCircuitBreaker() throws InterruptedException {
        //System.out.println(1 / 0);
        //return Response.ok("Everything is good.");
        System.out.println("test invoked");
        Thread.sleep(10000);
        return Response.error(ErrorCode.SYSTEM_ERROR, "system error occurred");
    }*/

}
