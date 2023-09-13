
package com.Blog.service.Impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.Blog.constants.EmailConstant;
import com.Blog.model.dto.login.EmailReq;
import com.Blog.model.pojo.User;
import com.Blog.service.SendMailService;
import com.Blog.service.UserService;
import com.Blog.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SendMailServiceImpl implements SendMailService {

    private final JavaMailSender javaMailSender;
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
    public void sendMail() {
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


    @Value("${spring.mail.username}")
    private String sendMailer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final RedisUtil redisUtil;
    @Override
    public Boolean doSendEmailCode(EmailReq emailReq) {
        String sendTo = emailReq.getSendTo();
        String subject = emailReq.getSubject();
        String[] emails = sendTo.split(",");
        if (StrUtil.isBlank(sendTo) || !isEmails(emails)) return false;
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sendMailer);
            helper.setTo(emails);
            String code = RandomUtil.randomNumbers(6);
            switch (subject) {
                case EmailConstant.EMAIL_CODE:
                    helper.setSubject("邮箱验证码");
                    helper.setText(String.format(EmailConstant.EMAIL_CODE_CONTEXT, code), true);
                    // TODO 将验证码存入redis，过期时间为5分钟
                    if (redisUtil.get(emailReq.getSendTo())==null){
                        redisUtil.setData(emailReq.getSendTo(),code);
                    }
                    break;
                case EmailConstant.EMAIL_NOTICE:
                    helper.setSubject("好消息，好消息!!!!!");
                    helper.setText(EmailConstant.EMAIL_NOTICE_CONTEXT, true);
                    break;
                default:
                    return false;
            }

            javaMailSender.send(message);
        } catch (MessagingException e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean isEmails(String... sendTo) {
        for (String email : sendTo) {
            if (!Validator.isEmail(email)) {
                return false;
            }
        }
        return true;
    }

}
