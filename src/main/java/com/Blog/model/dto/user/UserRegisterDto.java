package com.Blog.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterDto {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "再次输入密码不能为空")
    private String checkPassword;

    @NotBlank(message = "邮箱不能为空")
    private String mail;

    @NotBlank(message = "邮箱验证码不能为空")
    private String emailCode;

    @NotBlank(message = "图形验证码不能为空")
    private String code;

}
