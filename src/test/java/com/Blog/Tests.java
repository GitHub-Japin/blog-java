package com.Blog;

import com.Blog.dao.UserMapper;
import com.Blog.pojo.User;
import com.Blog.service.SendMailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest
class Tests {

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private UserMapper userMapper;
    @Test
    void TestMail() {
        try {
            sendMailService.sendMail();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addUser1000(){
        for (long i=0;i<100000;i++){
            userMapper.insert(new User(i,"user2"+i,"https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg","kqvafz39564@chacuo.net","96e79218965eb72c92a549dd5a330112",1,LocalDateTime.now(),0));
        }
    }
}
