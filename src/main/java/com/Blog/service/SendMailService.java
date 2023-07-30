package com.Blog.service;

import javax.mail.MessagingException;

public interface SendMailService {
    void sendMail() throws MessagingException;
}
