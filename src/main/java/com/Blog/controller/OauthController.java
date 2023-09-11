package com.Blog.controller;

import com.Blog.config.JustAuthProperties;
import com.Blog.constants.ResultConstant;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.scope.AuthGiteeScope;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthScopeUtils;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api("授权登录")
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final JustAuthProperties justAuthProperties;
    private final UserService userService;

    @RequestMapping("/render")
    public void renderAuth(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest();
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/gitee/callback")
    public Object login(AuthCallback callback) {
        AuthRequest authRequest = getAuthRequest();
        AuthResponse authResponse = authRequest.login(callback);
        //按需获取用户信息
        AuthUser oauthUser = (AuthUser) authResponse.getData();
        String username = oauthUser.getUsername();
        String email = oauthUser.getEmail();
        //TODO 随机给用户设置密码，并插入数据库

        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,username).or().eq(User::getEmail,email);
        User userExit = userService.getOne(lambdaQueryWrapper);
        if (userExit != null){
            return ResultConstant.UserExistMsg;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword("111111");
        userService.saveUser(user);

        return authRequest.login(callback);
    }

    private AuthRequest getAuthRequest() {
        return new AuthGiteeRequest(AuthConfig.builder()
                .clientId(justAuthProperties.getClientId())
                .clientSecret(justAuthProperties.getClientSecret())
                .redirectUri(justAuthProperties.getRedirectUri())
                .scopes(AuthScopeUtils.getScopes(AuthGiteeScope.USER_INFO,AuthGiteeScope.EMAILS))
                .build());
    }

}
