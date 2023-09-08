package com.Blog.common;

import com.Blog.constants.ResultConstant;
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
        res.msg = ResultConstant.SuccessMsg;
        res.code = ResultConstant.SuccessCode;
        return res;
    }

    //异常处理
    public static <T> Result<T> error(String msg) {
        Result<T> res = new Result<>();
        res.msg = msg;
        res.code = ResultConstant.FailCode;
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
