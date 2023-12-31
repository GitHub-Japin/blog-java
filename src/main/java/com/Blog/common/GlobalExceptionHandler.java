package com.Blog.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500 运行错误
    @ExceptionHandler(value =  RuntimeException.class)
    public Result<String> handler(RuntimeException e){
        log.error("运行时异常:",e);
        return Result.error(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED) //401 没有权限
    @ExceptionHandler(value =  ShiroException.class)
    public Result<String> handler(ShiroException e){
        log.error("Shiro异常:",e);
        return Result.fail(401,"未登录或登录信息过期",null);
    }
}
