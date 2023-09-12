package com.Blog;

import com.Blog.dao.UserMapper;
import com.Blog.model.pojo.User;
import com.Blog.service.SendMailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

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

    /*@Test
    void addUser1000(){
        for (long i=0;i<100000;i++){
            userMapper.insert(new User(i,"user2"+i,"https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg","kqvafz39564@chacuo.net","96e79218965eb72c92a549dd5a330112",1,LocalDateTime.now(),0));
        }
    }*/


    @Test
    void testTime(){
        System.out.println(LocalDateTime.now());
        System.out.println(new Date());
    }

    @Test
    void addUser(){
        String password = DigestUtils.md5DigestAsHex((111111+"admin").getBytes());
        System.out.println(password);
    }


    @Test
    void deleteUser(){
        QueryWrapper<User> userLambdaQueryWrapper =new QueryWrapper<>();
        userLambdaQueryWrapper.gt("id",10);//  >10
        userLambdaQueryWrapper.eq("deleted",1); // =1
        userMapper.delete(userLambdaQueryWrapper);
        System.out.println("完成");
    }


    @Test
    void queryUser(){
        QueryWrapper<User> userLambdaQueryWrapper =new QueryWrapper<>();
        userLambdaQueryWrapper.eq("username","admin");
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        System.out.println(user.getPassword());

        String password = DigestUtils.md5DigestAsHex((111111+user.getSalt()).getBytes());
        System.out.println(password);

        System.out.println(user.getPassword().equals(password));
    }


    @Test
    void queryUser1(){
        String username="admin";
        String email="111";
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,username).or().eq(User::getEmail,email);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        System.out.println(user);
    }

    @Test
    void queryUser2(){//只查指定字段
        String username="admin";
        String email="111";
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,username).or().eq(User::getEmail,email);
        lambdaQueryWrapper.select(User::getUsername,User::getId);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        System.out.println(user);
    }

}
