package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import jakarta.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource 
    private RedisTemplate<String, Object> redisTemplate;

    @Test 
    void contextLoads() {
        redisTemplate.opsForValue().set("这是key", "这是值");
        String value = (String) redisTemplate.opsForValue().get("这是key");
        System.out.println(value);

        // redisTemplate.opsForValue().set("k2", "v2");
        value = (String) redisTemplate.opsForValue().get("keykey");
        System.out.println(value);
        // JDK默认序列化模式： string -> `\xac\xed\x00\x05t`
        // 所以，k2实际存储的是：`"\xac\xed\x00\x05t\x00\x02k2"`
        //  k2的内容v2，实际存的是：`"\xac\xed\x00\x05t\x00\x02v2"`

        Object obj = redisTemplate.opsForHash().get("spring:session:sessions:514e6820-02c5-4899-9fa1-5d4d3c7638b6", "lastAccessedTime");
        System.out.println(obj);
    }
}
