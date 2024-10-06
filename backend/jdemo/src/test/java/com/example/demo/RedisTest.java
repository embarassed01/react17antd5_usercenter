package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.example.demo.model.User;

import jakarta.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource 
    private RedisTemplate<String, Object> redisTemplate;

    @Test 
    void contextLoads() {
        // redisTemplate.opsForValue().set("这是key", "这是值");
        // String value = (String) redisTemplate.opsForValue().get("这是key");

        // // redisTemplate.opsForValue().set("k2", "v2");
        // value = (String) redisTemplate.opsForValue().get("keykey");
        // // JDK默认序列化模式： string -> `\xac\xed\x00\x05t`
        // // 所以，k2实际存储的是：`"\xac\xed\x00\x05t\x00\x02k2"`
        // //  k2的内容v2，实际存的是：`"\xac\xed\x00\x05t\x00\x02v2"`

        // Object obj = redisTemplate.opsForHash().get("spring:session:sessions:514e6820-02c5-4899-9fa1-5d4d3c7638b6", "lastAccessedTime");

        // redisTemplate.setKeySerializer(RedisSerializer.string());
        // redisTemplate.setValueSerializer(RedisSerializer.string());
        // redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // redisTemplate.setHashValueSerializer(RedisSerializer.string());

        ValueOperations valueOperations = redisTemplate.opsForValue();
        // ListOperations listOperations = redisTemplate.opsForList();
        // 增/改
        valueOperations.set("yupiString", "dog");
        valueOperations.set("yupiInt", 1);
        valueOperations.set("yupiDouble", 2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("yupi");
        valueOperations.set("yupiUser", user);
        // 查
        Assertions.assertTrue("dog".equals(valueOperations.get("yupiString")));
        Assertions.assertTrue(1 == (Integer) (valueOperations.get("yupiInt")));
        Assertions.assertTrue(2.0 == (Double) valueOperations.get("yupiDouble"));
        System.out.println(valueOperations.get("yupiUser"));
        // 删
        redisTemplate.delete("yupiInt");
    }
}
