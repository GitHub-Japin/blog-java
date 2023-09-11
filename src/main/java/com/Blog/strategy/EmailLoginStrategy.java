package com.Blog.strategy;

import com.Blog.constants.LoginStrategyConstant;
import com.Blog.model.dto.EmailLoginReq;
import com.Blog.strategy.core.AbstractExecuteStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class EmailLoginStrategy implements AbstractExecuteStrategy<EmailLoginReq,Boolean> {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    @Override
    public String mark() {
        return LoginStrategyConstant.EMAIL_LOGIN_MARK;
    }

    @Override
    public Boolean executeResp(EmailLoginReq requestParam) {

        //TODO 将验证码取出，与Redis中的进行对比，一致则登录成功，删除Redis验证码


        return AbstractExecuteStrategy.super.executeResp(requestParam);
    }
}
