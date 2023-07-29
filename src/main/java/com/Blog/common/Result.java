package com.Blog.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    @ApiModelProperty(value = "响应编码:200-请求处理成功，400-失败")
    private Integer code;//状态码 200代表成功

    @ApiModelProperty(value = "响应消息")
    private String msg;//返回消息信息

    @ApiModelProperty(value = "响应数据")
    private T data;//返回数据

    public static <T> Result<T> success(T data) {//成功
        Result<T> res = new Result<>();
        res.data = data;
        res.msg = "操作成功";
        res.code = 200;
        return res;
    }

    //异常处理
    public static <T> Result<T> error(String msg) {
        Result<T> res = new Result<>();
        res.msg = msg;
        res.code = 400;
        return res;
    }

    public static <T> Result<T> fail(int code,String msg,T data){
        Result<T> res = new Result<>();
        res.setCode(code);
        res.setMsg(msg);
        res.setData(data);
        return res;
    }

}
