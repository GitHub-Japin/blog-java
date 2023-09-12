package com.Blog.strategy;

import com.Blog.common.CustomException;
import com.Blog.constants.LoginStrategyConstant;
import com.Blog.model.dto.LoginDto;
import com.Blog.model.dto.UserNameLoginReq;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import com.Blog.strategy.core.AbstractExecuteStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class UserNameLoginStrategy implements AbstractExecuteStrategy<UserNameLoginReq,Boolean> {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final UserService userService;
    @Override
    public String mark() {
        return LoginStrategyConstant.USERNAME_LOGIN_MARK;
    }

    @Override
    public Boolean executeResp(UserNameLoginReq requestParam) {

        //TODO 与数据库的账号密码进行验证,并且生成JWTToken返回给前端 (將原來代码复制到这)
        if (StringUtils.isEmpty(requestParam.getUsername())
                || StringUtils.isEmpty(requestParam.getPassword())
                ||StringUtils.isEmpty(requestParam.getCode())){
            throw new CustomException("输入不能有空");
        }
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(requestParam.getUsername());
        loginDto.setPassword(requestParam.getPassword());
        userService.loginWithSalt(loginDto,response);


        return AbstractExecuteStrategy.super.executeResp(requestParam);
    }
}
