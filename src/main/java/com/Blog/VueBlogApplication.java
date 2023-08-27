package com.Blog;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;

@Slf4j
@EnableTransactionManagement//事务
@SpringBootApplication
@EnableCaching//开启缓冲注解功能
@EnableScheduling//开启定时任务功能
@MapperScan("com.Blog.dao")
public class VueBlogApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(VueBlogApplication.class, args);
        String serverPort = context.getEnvironment().getProperty("server.port");
        System.out.println("(♥◠‿◠)ﾉﾞ  启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
        "接口文档： http://localhost:" + serverPort + "/doc.html" + "\n" +
        "         http://" + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort + "/doc.html"+"\n"+
        "用户端    http://localhost:8080/#/blogs" + "\n" +
        "管理端    http://localhost:8080/#/serverinfo" + "\n");
    }
}
