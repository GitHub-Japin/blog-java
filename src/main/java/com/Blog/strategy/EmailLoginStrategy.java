package com.Blog.strategy;

import com.Blog.common.CustomException;
import com.Blog.constants.EmailConstant;
import com.Blog.constants.LoginStrategyConstant;
import com.Blog.model.dto.login.EmailLoginDto;
import com.Blog.model.dto.login.EmailReq;
import com.Blog.model.dto.login.UserNameLoginDto;
import com.Blog.model.pojo.User;
import com.Blog.service.SendMailService;
import com.Blog.service.UserService;
import com.Blog.strategy.core.AbstractExecuteStrategy;
import com.Blog.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class EmailLoginStrategy implements AbstractExecuteStrategy<EmailLoginDto,User> {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final StringRedisTemplate redisTemplate;

    private final UserService userService;
    private final SendMailService mailService;
    @Override
    public String mark() {
        return LoginStrategyConstant.EMAIL_LOGIN_MARK;
    }

    private final RedisUtil redisUtil;
    @Override
    public User executeResp(EmailLoginDto requestParam) {
        if (requestParam.getEmail()==null||requestParam.getEmailCode()==null||requestParam.getPCode()==null){
            throw new CustomException("输入不能为空");
        }
        //TODO 将验证码取出，与Redis中的进行对比，一致则登录成功，删除Redis验证码
        String value = redisUtil.get(requestParam.getEmail());
        if (!StringUtils.equalsIgnoreCase(value,requestParam.getEmailCode())){
            throw new CustomException("验证码错误");
        }
        redisTemplate.delete(requestParam.getEmail() + "_code");
        EmailReq emailReq = new EmailReq();
        emailReq.setSubject(EmailConstant.EMAIL_CODE);
        emailReq.setSendTo(requestParam.getEmail());
//        mailService.doSendEmailCode(emailReq);

        return userService.loginWithEmail(requestParam, response);
    }
}
