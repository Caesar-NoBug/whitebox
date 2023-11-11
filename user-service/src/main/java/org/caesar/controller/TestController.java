package org.caesar.controller;

import org.caesar.common.Response;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(
        origins = "http://localhost:8081",
        allowedHeaders = {"token", "Content-Type"},
        allowCredentials = "true"
)
@RestController
public class TestController {

    @GetMapping("/test")
    public String testAccessible(){
        return "test is success";
    }

    @PreAuthorize("hasAuthority('sys:blog:list')")
    @GetMapping("/test1")
    public String test1(){
        return "test list is success";
    }

    @GetMapping("/test2")
    public Response test2(){
        return Response.ok(null, "test 2 is success");
    }
}
