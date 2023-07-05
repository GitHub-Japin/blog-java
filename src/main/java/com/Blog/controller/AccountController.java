package com.Blog.controller;

import com.Blog.pojo.LoginDto;
import com.Blog.common.Result;
import com.Blog.jwt.JwtUtil;
import com.Blog.pojo.User;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


//账号的登录和退出
@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    //账号登录
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", loginDto.getUsername());
        User user = userService.getOne(qw);
        if (user == null) {
            return Result.error("账号不存在");
        }
        String password = DigestUtils.md5DigestAsHex(loginDto.getPassword().getBytes());
        if (!user.getPassword().equals(password)) {
            return Result.error("密码不正确");
        }
        if(user.getStatus().equals(1)){
            return Result.error("账号已被禁用");
        }
        String jwt = jwtUtil.generateToken(user.getId());
        response.setHeader("Authorization", jwt);

        return Result.success(user);
    }


    @RequiresAuthentication
    @PostMapping("/logout")
    public Result<String> logout() {//退出登录
        SecurityUtils.getSubject().logout();
        return Result.success("注销成功");
    }
}
