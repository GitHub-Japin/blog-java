package com.Blog;

import cn.hutool.core.io.FileUtil;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import java.io.DataInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


public class JwtTest {
    @Test
    public void testCreateJwt() {//不使用签名
        Map headers = new HashMap();
        headers.put("alg", "none");//不使用签名算法
        headers.put("typ", "JWT");//指定令牌类型

        Map body = new HashMap();
        body.put("userId", "100");
        body.put("account", "admin");
        body.put("role", "admin");

        //使用JJwt提供api生成令牌
        String jwt = Jwts.builder().setHeader(headers).setClaims(body).setId("101").compact();
        System.out.println(jwt);
        //解析jwt
        Jwt res = Jwts.parser().parse(jwt);
        Header header = res.getHeader();
        Object body1 = res.getBody();
        System.out.println(header);
        System.out.println(body1);
    }
    @Test
    public void testCreateJwtWithSign() {//使用签名
        Map headers = new HashMap();
        headers.put("alg", SignatureAlgorithm.HS256.getValue());//使用签名算法,有无getValue无差别
        headers.put("typ", "JWT");//指定令牌类型

        Map body = new HashMap();
        body.put("userId", "100");
        body.put("account", "admin");
        body.put("role", "admin");

        //使用JJwt提供api生成令牌
        String jwt = Jwts.builder().setHeader(headers).setClaims(body).setId("101").signWith(SignatureAlgorithm.HS256,"Japin").compact();
        System.out.println(jwt);
        //解析jwt
        Jwt res = Jwts.parser().setSigningKey("Japin").parse(jwt);
        Header header = res.getHeader();
        Object body1 = res.getBody();
        System.out.println(header);
        System.out.println(body1);
    }

    //生成jwt时使用签名算法生成签名部分----基于RS256签名算法
    @Test
    public void test3() throws Exception{
        //添加构成JWT的参数
        Map<String, Object> headMap = new HashMap();
        headMap.put("alg", SignatureAlgorithm.RS256.getValue());//使用RS256签名算法
        headMap.put("typ", "JWT");

        Map body = new HashMap();
        body.put("userId","1");
        body.put("username","xiaoming");
        body.put("role","admin");

        String jwt = Jwts.builder()
                .setHeader(headMap)
                .setClaims(body)
                .setId("jwt001")
                .signWith(SignatureAlgorithm.RS256,getPriKey())
                .compact();
        System.out.println(jwt);

        //解析jwt
        Jwt result = Jwts.parser().setSigningKey(getPubKey()).parse(jwt);
        Object jwtBody = result.getBody();
        Header header = result.getHeader();

        System.out.println(result);
        System.out.println(jwtBody);
        System.out.println(header);
    }

    //获取私钥
    public PrivateKey getPriKey() throws Exception{
        InputStream resourceAsStream =
                this.getClass().getClassLoader().getResourceAsStream("pri.key");
        DataInputStream dis = new DataInputStream(resourceAsStream);
        byte[] keyBytes = new byte[resourceAsStream.available()];
        dis.readFully(keyBytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    //获取公钥
    public PublicKey getPubKey() throws Exception{
        InputStream resourceAsStream =
                this.getClass().getClassLoader().getResourceAsStream("pub.key");
        DataInputStream dis = new DataInputStream(resourceAsStream);
        byte[] keyBytes = new byte[resourceAsStream.available()];
        dis.readFully(keyBytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    //生成自己的 秘钥/公钥 对
    @Test
    public void test4() throws Exception{
        //自定义 随机密码,  请修改这里
        String password = "itcast";

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();

        FileUtil.writeBytes(publicKeyBytes, "d:\\pub.key");
        FileUtil.writeBytes(privateKeyBytes, "d:\\pri.key");
    }
}