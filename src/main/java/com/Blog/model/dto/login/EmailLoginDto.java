package com.Blog.model.dto.login;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EmailLoginDto {

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "邮箱验证码不能为空")
    private String emailCode;

    @NotBlank(message = "图形验证码不能为空")
    private String pCode;

}
