package com.Blog.model.dto.login;

import com.Blog.constants.EmailConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EmailReq {
    /**
     * 接收方（如果有多人可用“,”隔开）
     */
    @NotBlank(message = "接收人不能为空")
    private String sendTo;

    /**
     * 邮件发送主题
     *    发送验证码：code
     *    发送通知：notice (默认)
     */
    private String subject = EmailConstant.EMAIL_NOTICE;
}
