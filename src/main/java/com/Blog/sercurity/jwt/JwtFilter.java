package com.Blog.sercurity.jwt;


import com.Blog.common.Result;
import com.Blog.constants.ResultConstant;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//登录处理JwtFilter继承shiro的AuthenticatingFilter
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtil jwtUtil;

    //创建JwtToken
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //1.从配置的请求头中取得Token
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("Authorization");
        if(token==null||token.length()==0){
            return null;
        }
        return new JwtToken(token);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String token = request.getHeader("Authorization");
        return !StringUtils.hasLength(token);
    }

    @Override //携带了Token时会执行该方法，拦截
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String token = request.getHeader("Authorization");
        Claims claims = jwtUtil.getClaimByToken(token);
        //校验是否为空和时间是否过期
        /*try {
            if(claims == null ||jwtUtil.isTokenExpired(claims.getExpiration())){//如果token过期
                SecurityUtils.getSubject().logout();
                throw new ExpiredCredentialsException("token已失效,请重新登录");
            }
            return executeLogin(servletRequest,servletResponse);
        } catch (Exception e) {
            return true;
        }*/
        if(StringUtils.isEmpty(token)){
            return true;
        }else{
            if(claims == null ||jwtUtil.isTokenExpired(claims.getExpiration())){//如果token过期
                SecurityUtils.getSubject().logout();
                throw new ExpiredCredentialsException(ResultConstant.TokenTTLMsg);
            }
            return executeLogin(servletRequest,servletResponse);
        }
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpServletResponse=(HttpServletResponse) response;
        String message = e.getMessage();

        String jsonMessage= JSON.toJSONString(Result.error(message));
        try {
            httpServletResponse.getWriter().write(jsonMessage);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return false;
    }
}
