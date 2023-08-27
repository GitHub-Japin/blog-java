package com.Blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

@Data
public class OptLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String annotation;           // 注解
    private String methodName;          // 方法名称
    private String args;                // 方法参数
    private String clientIp;            // 客户端请求IP地址
    private String uri;                 // 请求地址
    private String restfulName;         // 请求方式method,post,get等
    private Date time;                 // 请求时间
    private String httpStatusCode;     // 请求时httpStatusCode代码，如：200,400,404等
}
