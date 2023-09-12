package com.Blog.config;

import com.Blog.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
public class TaskConfig {

    @Autowired
    private SendMailService sendMailService;
    /**
     * 每天0点执行，秒、分钟、小时、天、月和星期
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void sendMail() throws MessagingException {
        sendMailService.sendMail();
    }
}
