package com.Blog.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public void setData(String email, String data) {
        redisTemplate.opsForValue().set(email + "_code", data, 5, TimeUnit.MINUTES);
    }

    public String get(String email) {
        return redisTemplate.opsForValue().get(email + "_code");
    }

    public Boolean delete(String email) {
        return redisTemplate.delete(email + "_code");
    }
}
