package com.Blog.sercurity.jwt;


//由于使用JwtToken，不是使用Shiro所提供的UsernamePasswordToken
//用于生成Token，通过Token得到用户信息，
import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Date;


@Data
@Component
@ConfigurationProperties(prefix = "jwt.configuration")
public class JwtUtil {
    //有效期
    private long expire;
    //密钥
    private String secret;

    //生成(获取)JwtToken，登录时调用
    public String generateToken(long userId){
        JwtBuilder builder = Jwts.builder();
        return builder.setHeaderParam("typ","Jwt") //头部
                .setHeaderParam("alg","HS256")
                .setSubject(userId+"")//体
                .setExpiration(new Date(System.currentTimeMillis()+expire*1000))//过期时间1分钟
                .signWith(SignatureAlgorithm.HS256,secret)//标签加密算法
                .compact();
    }

    //获取JwtToken内的信息，校验
    public Claims getClaimByToken(String token){
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token) //在这会判断下Token的有效性
                    .getBody();
        }catch (Exception e){
//            System.out.println("校验错误");
            return null;
        }
    }

    //判断Token是否到期
    public boolean isTokenExpired(Date expiration){
        return expiration.before(new Date());
    }
}
