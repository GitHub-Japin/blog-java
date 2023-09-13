package com.Blog.strategy;

import com.Blog.common.CustomException;
import com.Blog.common.Result;
import com.Blog.constants.LoginStrategyConstant;
import com.Blog.model.dto.login.UserNameLoginDto;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import com.Blog.strategy.core.AbstractExecuteStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class UserNameLoginStrategy implements AbstractExecuteStrategy<UserNameLoginDto, User> {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final UserService userService;
    @Override
    public String mark() {
        return LoginStrategyConstant.USERNAME_LOGIN_MARK;
    }

    @Override
    public User executeResp(UserNameLoginDto requestParam) {
        //TODO 与数据库的账号密码进行验证,并且生成JWTToken返回给前端 (將原來代码复制到这)
        if (StringUtils.isEmpty(requestParam.getUsername())
                || StringUtils.isEmpty(requestParam.getPassword())){
            throw new CustomException("输入不能有空");
        }
        UserNameLoginDto loginDto = new UserNameLoginDto();
        loginDto.setUsername(requestParam.getUsername());
        loginDto.setPassword(requestParam.getPassword());
        return userService.loginWithSalt1(loginDto, response);
    }
}
