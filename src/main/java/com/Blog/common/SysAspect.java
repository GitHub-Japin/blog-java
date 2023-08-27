package com.Blog.common;

import com.Blog.annotation.MyLog;
import com.Blog.dao.OptLogMapper;
import com.Blog.jwt.JwtUtil;
import com.Blog.pojo.OptLog;
import com.alibaba.fastjson.JSON;
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
        //需要通过解析seesion或token 获取

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
        List<Object> collect = Arrays.stream(args).filter(s -> !(s instanceof HttpServletRequest || s instanceof HttpServletResponse)).collect(Collectors.toList());
        optLog.setArgs(JSON.toJSONString(collect));

        //通过工具类获取Request对象
        RequestAttributes reqAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) reqAttributes;
        HttpServletRequest request = sra.getRequest();
        //访问的urL
        String url = request.getRequestURI().toString();
        optLog.setUri(url);
        //请求方式
        String restfulName = request.getMethod();
        optLog.setRestfulName(restfulName);
        //登录IP
        String ipAddr = getIpAddr(request);
        optLog.setClientIp(ipAddr);

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

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
