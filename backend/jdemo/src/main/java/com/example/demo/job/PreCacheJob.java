package com.example.demo.job;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

    @Resource 
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(7L);
    
    // 每天执行，加载预热推荐用户
    @Scheduled(cron = "32 18 11 * * *") 
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {  // 只有一个线程能获取到锁
            if (lock.tryLock(0, 30000L, TimeUnit.MILLISECONDS)) {  // 尝试获取锁，每天只执行一次，所以等待时间一定是0，没有获取到就不要再来尝试了，等第二天吧  // leaseTime30000是过期时间
                System.out.println("getLock: " + Thread.currentThread().getId());
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
        } catch (InterruptedException e) {
            // e.printStackTrace();
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();  // 释放锁
            }
        }
    }
}
