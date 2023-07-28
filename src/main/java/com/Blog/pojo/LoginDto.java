package com.Blog.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

//用于账号的校验
@Data
@ApiModel(value = "LoginDto",description = "登录数据传输实体")
public class LoginDto {
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String password;
}
