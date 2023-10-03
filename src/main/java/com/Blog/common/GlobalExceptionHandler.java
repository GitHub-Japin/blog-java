package com.Blog.common;

import com.Blog.constants.ResultConstant;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
//        log.error("运行时异常:",e);
        return Result.error(ResultConstant.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED) //401 没有权限
    @ExceptionHandler(value = ShiroException.class)
    public Result<String> handler(ShiroException e) {
//        log.error("Shiro异常:",e);
        return Result.fail(ResultConstant.UNAUTHORIZED, ResultConstant.UNAUTHORIZEDMsg, null);
    }

    /**
     * hibernate validator 数据绑定验证异常拦截
     *
     * @param e 绑定验证异常
     * @return 错误返回消息
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> validateErrorHandler(MethodArgumentNotValidException e) {
        ObjectError error = e.getBindingResult().getAllErrors().get(0);
        log.info("数据验证异常：{}", error.getDefaultMessage());
        return Result.error(error.getDefaultMessage());
    }

}
