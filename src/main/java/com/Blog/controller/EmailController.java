package com.Blog.controller;

import com.Blog.model.dto.EmailReq;
import com.Blog.service.SendMailService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api("发送邮件")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final SendMailService sendMailService;

    @PostMapping("/codeOrNotice")
    public Boolean sendEmailCode(@RequestBody EmailReq emailReq){
        return sendMailService.doSendEmailCode(emailReq);
    }

}
