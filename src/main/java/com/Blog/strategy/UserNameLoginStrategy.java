package com.Blog.strategy;

import com.Blog.constants.LoginStrategyConstant;
import com.Blog.model.dto.UserNameLoginReq;
import com.Blog.strategy.core.AbstractExecuteStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class UserNameLoginStrategy implements AbstractExecuteStrategy<UserNameLoginReq,Boolean> {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    @Override
    public String mark() {
        return LoginStrategyConstant.USERNAME_LOGIN_MARK;
    }

    @Override
    public Boolean executeResp(UserNameLoginReq requestParam) {

        //TODO 与数据库的账号密码进行验证,并且生成JWTToken返回给前端 (將原來代码复制到这)


        return AbstractExecuteStrategy.super.executeResp(requestParam);
    }
}
