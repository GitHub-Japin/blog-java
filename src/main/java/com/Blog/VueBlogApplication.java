package com.Blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;

@Slf4j
@EnableTransactionManagement//事务
@SpringBootApplication
@EnableCaching//开启缓冲注解功能
public class VueBlogApplication {
    public static void main(String[] args) throws Exception {
        log.info("启动成功");
        ConfigurableApplicationContext context = SpringApplication.run(VueBlogApplication.class, args);
        String serverPort = context.getEnvironment().getProperty("server.port");
        int port = Integer.parseInt(serverPort)-1;
        System.out.println("(♥◠‿◠)ﾉﾞ  启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              \n" +
                "                                    \n"+
        "接口文档： http://localhost:" + serverPort + "/doc.html" + "\n" +
        "         http://" + InetAddress.getLocalHost().getHostAddress() + ":" + serverPort + "/doc.html"+"\n"+
        "用户端    http://localhost:8080/#/blogs" + "\n" +
        "管理端    http://localhost:8080/#/serverinfo" + "\n");
    }
}
