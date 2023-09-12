package com.Blog.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OptLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String userName;              //操作用户名
    private String annotation;           // 注解
    private String methodName;          // 方法名称
    private String args;                // 方法参数
    private String clientIp;            // 客户端请求IP地址
    private String uri;                 // 请求地址
    private String restfulName;         // 请求方式method,post,get等

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date time;                 // 请求时间

    private String httpStatusCode;     // 请求时httpStatusCode代码，如：200,400,404等
}
