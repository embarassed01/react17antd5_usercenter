package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;

import jakarta.annotation.Resource;

@SpringBootTest
public class SampleTest {
    @Resource
    // private UserMapper userMapper;

    @Test
    public void testSelect() {
        // System.out.println("----selectAll method test----");
        // List<User> userList = userMapper.selectList(null);
        // Assertions.assertEquals(5, userList.size());
        // userList.forEach(System.out::println);
    }

}
