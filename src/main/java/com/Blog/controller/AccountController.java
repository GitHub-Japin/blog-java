package com.Blog.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.lang.Validator;
import com.Blog.annotation.MyLog;
import com.Blog.common.CustomException;
import com.Blog.constants.LoginStrategyConstant;
import com.Blog.constants.ResultConstant;
import com.Blog.model.dto.EmailLoginReq;
import com.Blog.model.dto.LoginDto;
import com.Blog.common.Result;
import com.Blog.model.dto.UserNameLoginReq;
import com.Blog.model.dto.UserRegisterReq;
import com.Blog.model.pojo.User;
import com.Blog.service.SendMailService;
import com.Blog.service.UserService;
import com.Blog.strategy.core.AbstractStrategyChoose;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//账号的登录和退出
@Api("用户登录与注册")
@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AbstractStrategyChoose strategyChoose;

    private final SendMailService sendMailService;
    @Autowired
    private UserService userService;

    /*@PostMapping("/login")
    @MyLog(name = "账号登录")
    public Result<User> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        return userService.loginWithSalt(loginDto, response);
    }*/


    @PostMapping("/login")
    public Boolean userLogin(@RequestBody @Valid UserNameLoginReq userNameLoginReq) {
        return strategyChoose.chooseAndExecuteResp(LoginStrategyConstant.USERNAME_LOGIN_MARK, userNameLoginReq);
    }

    @PostMapping("/email/login")
    public Boolean userLoginByEmail(@RequestBody @Valid EmailLoginReq emailLoginReq) {
        return strategyChoose.chooseAndExecuteResp(LoginStrategyConstant.EMAIL_LOGIN_MARK, emailLoginReq);
    }


    // Captcha验证码 参考{https://blog.csdn.net/netuser1937/article/details/131127004}
    @ApiOperation("生成验证码")
    @GetMapping("/verify")
    public void verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 生成Captcha验证码(定义图形验证码的长、宽、验证码字符数、干扰线宽度)
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 40, 5, 4);
        shearCaptcha.write(response.getOutputStream());//图形验证码写出，可以写出到文件，也可以写出到流
        // 用于后续验证用户输入验证码是否正确，用完可以移除
        request.getSession().setAttribute("verifyCode",shearCaptcha.getCode());
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/register")
    public Boolean userRegister(@RequestBody @Valid UserRegisterReq userRegisterReq) {
        //TODO 转移到service中
        String password = userRegisterReq.getPassword();
        String checkPassword = userRegisterReq.getCheckPassword();
        //校验两次密码是否一致
        if (!password.equals(checkPassword)) {
            return false;
        }

        String username = userRegisterReq.getUsername();
        String mail = userRegisterReq.getMail();
        if (!Validator.isEmail(mail)) return false;
        //TODO 查询数据库校验用户名和mail已被注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username).or().eq(User::getEmail, mail);
        User user = userService.getOne(queryWrapper);
        if (user != null) {
            throw new CustomException("用户名或邮箱已存在");
        }

        //TODO Redis校验验证码
        //Redis key:email_code:邮箱  value: code
        String code = userRegisterReq.getEmailCode();
        String codeValue = redisTemplate.opsForValue().get(mail + "_code");
        if (!codeValue.equals(code)){
            throw new CustomException("验证码错误");
        }

        //TODO 插入数据库，如果成功删除Redis中验证码
        redisTemplate.delete(mail + "_code");
        User targetUser =new User();
        BeanUtils.copyProperties(targetUser, userRegisterReq);
        userService.saveUser(targetUser);

        return true;
    }
    @RequiresAuthentication
    @PostMapping("/logout")
    @MyLog(name = "账号退出")
    public Result<String> logout() {//退出登录
        return userService.logout();
    }
}
