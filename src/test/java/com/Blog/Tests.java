package com.Blog;

import com.Blog.service.SendMailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;

import javax.mail.MessagingException;

@SpringBootTest
class Tests {

    @Autowired
    private SendMailService sendMailService;

    @Test
    void TestMail() {
        try {
            sendMailService.sendMail();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}
