package com.Blog.common;

public class CustomException extends RuntimeException{//自定义业务异常
    public CustomException(String message){
        super(message);
    }
}
