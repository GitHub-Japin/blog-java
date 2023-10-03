package com.Blog.controller;

import com.Blog.common.Result;
import com.Blog.config.JustAuthProperties;
import com.Blog.model.dto.login.UserNameLoginDto;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.scope.AuthGiteeScope;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthScopeUtils;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {

    private final JustAuthProperties justAuthProperties;
    private final UserService userService;

    @GetMapping("/render")
    public void renderAuth(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest();
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    @RequestMapping("/gitee/callback")
    public Object login(AuthCallback callback, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest();
        AuthResponse authResponse = authRequest.login(callback);
        //按需获取用户信息
        AuthUser oauthUser = (AuthUser) authResponse.getData();
        String username = oauthUser.getUsername();
        String email = oauthUser.getEmail();
        String avatar = oauthUser.getAvatar();

        //TODO 随机给用户设置密码，并插入数据库
        User user = new User();
        user.setEmail(email);
        user.setAvatar(avatar);
        user.setUsername("Gitee_" + username);
        user.setPassword("111111");
        userService.saveUser(user);
        // TODO
        // 这里最后改成重定向，把token保留好之后跳回去
        AuthToken token = oauthUser.getToken();
        String accessToken = token.getAccessToken();
//        String oauthToken = token.getOauthToken();
        String refreshToken = token.getRefreshToken();
        if (authResponse.ok()) {
            UserNameLoginDto dto = new UserNameLoginDto();
            dto.setUsername("Gitee_" + username);
            dto.setPassword("111111");
            userService.loginWithSalt1(dto, response);
            response.setHeader("Authorization", refreshToken);

            return user;
        } else {
            return "error";
        }

//        return authResponse;
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
