package org.caesar.controller;

import org.caesar.common.vo.Response;
import org.caesar.domain.user.vo.UserMinVO;
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

    @GetMapping("/min")
    public Response<Map<Long, UserMinVO>> getUserMin(@RequestParam List<Long> id) {
        return Response.ok(userService.getUserMin(id));
    }

}
