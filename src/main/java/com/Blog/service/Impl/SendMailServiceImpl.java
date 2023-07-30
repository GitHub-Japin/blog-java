
package com.Blog.service.Impl;

import com.Blog.pojo.User;
import com.Blog.service.SendMailService;
import com.Blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UserService userService;

    //发送人
    @Value("${Japin-Config.mail_user}")
    private String from;
    //标题
    private final String subject = "感谢邮件";
    //正文
    private final String context = "感谢你使用Blog系统";

    @Override
    public void sendMail() throws MessagingException {
        List<User> userList = userService.list();
        for (User user : userList) {
            String email = user.getEmail();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from+"(博客系统管理员)");
            message.setTo(email);
            message.setSubject(subject);
            message.setText(context);
            javaMailSender.send(message);
        }
    }
}
