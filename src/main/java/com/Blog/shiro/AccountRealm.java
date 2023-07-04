package com.Blog.shiro;

import com.Blog.jwt.JwtToken;
import com.Blog.jwt.JwtUtil;
import com.Blog.pojo.User;
import com.Blog.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountRealm extends AuthorizingRealm {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    //使得shiro支持Jwt的Token 没有使用UsernamePasswordToken
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    //授权(登录后的权限)
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    //认证(登录executeLogin的时候才会执行的逻辑)
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException{
        JwtToken jwtToken = (JwtToken) authenticationToken;
        //1.获取用户id
        String userId = jwtUtil.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        //2.查询用户信息
        User user = userService.getById(userId);
        //3.验证用户信息
        if(user == null){
            throw new UnknownAccountException("用户不存在");
        }
        if(user.getStatus() == -1){
            throw new LockedAccountException("用户处于锁定状态");
        }

        return new SimpleAuthenticationInfo(user,jwtToken.getCredentials(),getName());
    }

    /*简单的equal匹配
    * public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        Object tokenCredentials = this.getCredentials(token);
        Object accountCredentials = this.getCredentials(info);
        return this.equals(tokenCredentials, accountCredentials);
    }*/

}
