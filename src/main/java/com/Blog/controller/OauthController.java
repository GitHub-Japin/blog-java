package com.Blog.controller;

import com.Blog.config.JustAuthProperties;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
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
        User user = new User();
        user.setEmail(email);
        user.setUsername("Gitee_"+username);
        user.setPassword("111111");
        userService.saveUser(user);

        return authResponse;
    }

    private AuthRequest getAuthRequest() {//创建Request
        return new AuthGiteeRequest(AuthConfig.builder()
                .clientId(justAuthProperties.getClientId())//客户端ID
                .clientSecret(justAuthProperties.getClientSecret())//客户端密钥
                .redirectUri(justAuthProperties.getRedirectUri())//重定向地址
                .scopes(AuthScopeUtils.getScopes(AuthGiteeScope.USER_INFO,AuthGiteeScope.EMAILS))//设置信息
                .build());
    }

}
