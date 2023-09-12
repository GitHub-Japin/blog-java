package com.Blog.sercurity.shiro;

import com.Blog.constants.ResultConstant;
import com.Blog.sercurity.jwt.JwtToken;
import com.Blog.sercurity.jwt.JwtUtil;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
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
        System.out.println("执行了=>授权doGetAuthorizationInfo");
        return null;
    }

    //认证(登录executeLogin的时候才会执行的逻辑)
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException{
        System.out.println("执行了=>认证doGetAuthenticationInfo");
        JwtToken jwtToken = (JwtToken) authenticationToken;
        //1.获取用户id
        String userId = jwtUtil.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        //2.查询用户信息
        User user = userService.getById(userId);
        //3.验证用户信息
        if(user == null){
            throw new UnknownAccountException(ResultConstant.UserNotExitMsg);
        }
        if(user.getStatus() == -1){
            throw new LockedAccountException(ResultConstant.UserLockMsg);
        }
        return new SimpleAuthenticationInfo(user,jwtToken.getCredentials(),getName());
    }

}
