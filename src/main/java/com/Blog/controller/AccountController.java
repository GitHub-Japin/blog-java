package com.Blog.controller;

import com.Blog.annotation.MyLog;
import com.Blog.model.dto.LoginDto;
import com.Blog.common.Result;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

//账号的登录和退出
@RestController
public class AccountController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @MyLog(name = "账号登录")
    public Result<User> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        return userService.login(loginDto, response);
    }

    @RequiresAuthentication
    @PostMapping("/logout")
    @MyLog(name = "账号退出")
    public Result<String> logout() {//退出登录
        return userService.logout();
    }
}
