package org.caesar.controller;

import org.caesar.common.context.ContextHolder;
import org.caesar.common.vo.Response;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.domain.user.vo.UserPreferVO;
import org.caesar.service.UserExtraService;
import org.caesar.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserExtraService userExtraService;

    @GetMapping("/min")
    public Response<Map<Long, UserMinVO>> getUserMin(@RequestParam List<Long> id) {
        return Response.ok(userService.getUserMin(id));
    }

    @GetMapping("/prefer")
    public Response<UserPreferVO> getUserPrefer() {
        long userId = ContextHolder.getUserId();
        return Response.ok(userExtraService.getUserPrefer(userId));
    }

}
