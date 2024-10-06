package com.example.demo.job;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 预热缓存 job
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource 
    private UserService userService;

    @Resource 
    private RedisTemplate<String, Object> redisTemplate;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(7L);
    
    // 每天执行，加载预热推荐用户
    @Scheduled(cron = "32 33 21 * * *") 
    public void doCacheRecommendUser() {
        for (Long userId : mainUserList) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> userPage = userService.page(new Page<>(1, 100), queryWrapper);
            String redisKey = String.format("yupao:user:recommend:%d", userId);
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            // 写缓存
            try {
                valueOperations.set(redisKey, userPage, 100000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("redis set key error", e);
            }
        }        
    }
}
