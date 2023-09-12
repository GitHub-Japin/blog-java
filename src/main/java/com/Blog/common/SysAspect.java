package com.Blog.common;

import com.Blog.annotation.MyLog;
import com.Blog.dao.OptLogMapper;
import com.Blog.model.pojo.OptLog;
import com.Blog.model.pojo.User;
import com.Blog.service.UserService;
import com.Blog.utils.IpAddressUtil;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class SysAspect {
    @Autowired
    private OptLogMapper optLogMapper;

    @Pointcut("@annotation(com.Blog.annotation.MyLog)")//指定范围为+Mylog注解的
    private void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        OptLog optLog=new OptLog();
        //获取用户名
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if(user!=null){
            optLog.setUserName(user.getUsername());
        }else {
            optLog.setUserName("访客");
        }

        //获取增强类和方法的信息
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        //获取被增强的方法对象
        Method method = methodSignature.getMethod();
        //从方法中解析注解
        if (method != null) {
            MyLog LogAnnotation = method.getAnnotation(MyLog.class);
            optLog.setAnnotation(LogAnnotation.name());
        }
        // 方法名字
        String methodName = method.getName();
        optLog.setMethodName(methodName);

        // 方法参数
        Object[] args = joinPoint.getArgs();
        List<Object> collect = Arrays.stream(args)
                        .filter(s -> !(s instanceof HttpServletRequest
                                || s instanceof HttpServletResponse))
                        .collect(Collectors.toList());
        optLog.setArgs(JSON.toJSONString(collect));

        //通过工具类获取Request对象
        RequestAttributes reqAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) reqAttributes;
        HttpServletRequest request = sra.getRequest();
        //访问的urL
        String url = request.getRequestURI();
        optLog.setUri(url);
        //请求方式
        String restfulName = request.getMethod();
        optLog.setRestfulName(restfulName);
        //登录IP
        String ip = IpAddressUtil.getIpAddress(request);
        optLog.setClientIp(ip);

        //操作时间
        optLog.setTime(new Date());

        // 返回值
        HttpServletResponse response =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        int httpStatusCode = response.getStatus();
        optLog.setHttpStatusCode(httpStatusCode + "");

        //保存到数据库
        optLogMapper.insert(optLog);
        return joinPoint.proceed();
    }
}
