package com.Blog.common;

import lombok.Data;

//用于账号的校验
@Data
public class LoginDto {

    private String username;

    private String password;
}
