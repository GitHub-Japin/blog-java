package com.Blog.controller;

import com.Blog.constants.LoginStrategyConstant;
import com.Blog.model.dto.login.EmailLoginDto;
import com.Blog.common.Result;
import com.Blog.model.dto.login.UserNameLoginDto;
import com.Blog.model.dto.user.UserRegisterDto;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import com.Blog.strategy.core.AbstractStrategyChoose;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api("用户登录与注册")
@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AbstractStrategyChoose strategyChoose;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody @Valid UserNameLoginDto loginDto) {
        return Result.success(strategyChoose
                .chooseAndExecuteResp(LoginStrategyConstant.USERNAME_LOGIN_MARK, loginDto));
    }

    @PostMapping("/loginByEmail")
    public Result<User> userLoginByEmail(@RequestBody @Valid EmailLoginDto emailLoginDto) {
        return Result.success(strategyChoose
                .chooseAndExecuteResp(LoginStrategyConstant.EMAIL_LOGIN_MARK, emailLoginDto));
    }

    @PostMapping("/sendEmailCode")
    public Boolean sendEmailCode(HttpServletRequest request,@RequestBody @Valid EmailLoginDto emailLoginDto) {
        return userService.sendEmailCode(request, emailLoginDto);
    }

    @GetMapping("/verify")
    public void verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        userService.verify(request, response);
    }


    @PostMapping("/register")
    public Boolean userRegister(@RequestBody @Valid UserRegisterDto userRegisterDto) {
       return userService.userRegister(userRegisterDto);
    }

    @RequiresAuthentication
    @PostMapping("/logout")
    public Result<String> logout() {//退出登录
        return userService.logout();
    }
}
