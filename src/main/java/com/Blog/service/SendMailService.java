package com.Blog.service;

import com.Blog.model.dto.EmailReq;

import javax.mail.MessagingException;

public interface SendMailService {
    void sendMail() throws MessagingException;
    Boolean doSendEmailCode(EmailReq emailReq);
}
