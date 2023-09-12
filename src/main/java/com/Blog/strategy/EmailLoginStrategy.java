package com.Blog.strategy;

import com.Blog.common.CustomException;
import com.Blog.constants.LoginStrategyConstant;
import com.Blog.model.dto.login.EmailLoginDto;
import com.Blog.strategy.core.AbstractExecuteStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class EmailLoginStrategy implements AbstractExecuteStrategy<EmailLoginDto,Boolean> {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final StringRedisTemplate redisTemplate;
    @Override
    public String mark() {
        return LoginStrategyConstant.EMAIL_LOGIN_MARK;
    }

    @Override
    public Boolean executeResp(EmailLoginDto requestParam) {

        //TODO 将验证码取出，与Redis中的进行对比，一致则登录成功，删除Redis验证码
        String value = redisTemplate.opsForValue().get(requestParam.getEmail() + "_code");
        if (StringUtils.equalsIgnoreCase(value,requestParam.getEmailCode())){
            throw new CustomException("验证码错误");
        }
        redisTemplate.delete(requestParam.getEmail() + "_code");

        return AbstractExecuteStrategy.super.executeResp(requestParam);
    }
}
