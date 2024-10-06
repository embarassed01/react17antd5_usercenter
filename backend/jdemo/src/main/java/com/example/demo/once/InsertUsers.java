package com.example.demo.once;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;

import jakarta.annotation.Resource;

/**
 * 一次性定时任务
 * @Component 注解，就能被springboot加载
 */
@Component
public class InsertUsers {

    @Resource 
    private UserMapper userMapper;

    private final String[] AvatarUrls = {  // 头像池：20个
        "https://api.dicebear.com/9.x/adventurer/svg",
        "https://api.dicebear.com/9.x/avataaars/svg",
        "https://api.dicebear.com/9.x/big-ears/svg",
        "https://api.dicebear.com/9.x/big-smile/svg",
        "https://api.dicebear.com/9.x/bottts/svg",
        "https://api.dicebear.com/9.x/croodles/svg",
        "https://api.dicebear.com/9.x/dylan/svg",
        "https://api.dicebear.com/9.x/fun-emoji/svg",
        "https://api.dicebear.com/9.x/glass/svg",
        "https://api.dicebear.com/9.x/icons/svg",
        "https://api.dicebear.com/9.x/identicon/svg",
        "https://api.dicebear.com/9.x/initials/svg",
        "https://api.dicebear.com/9.x/lorelei/svg",
        "https://api.dicebear.com/9.x/micah/svg",
        "https://api.dicebear.com/9.x/miniavs/svg",
        "https://api.dicebear.com/9.x/open-peeps/svg",
        "https://api.dicebear.com/9.x/pixel-art/svg",
        "https://api.dicebear.com/9.x/rings/svg",
        "https://api.dicebear.com/9.x/shapes/svg",
        "https://api.dicebear.com/9.x/thumbs/svg"
    };

    /**
     * 批量插入用户, 模拟
     * @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE) 单次任务
     */
    // @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    // @SuppressWarnings("deprecation")
    // public void doInsertUsers() {
    //     final int INSERT_NUM = 10000000; // 1000万

    //     StopWatch stopWatch = new StopWatch();
    //     stopWatch.start();

    //     for (int i = 0; i < 1000; i++) {
    //         int indexRandomAvatarUrls = (int) (Math.random() * (AvatarUrls.length - 1));
    //         int randomGender = (int) (Math.random() * (2 - 1));
    //         int randomUserRole = (int) (Math.random() * (2 - 1));

    //         User user = new User();
    //         user.setUsername(RandomStringUtils.random(5));
    //         user.setUserAccount(RandomStringUtils.randomAscii(6));
    //         user.setGender(randomGender);
    //         user.setUserPassword("b0dd3697a192885d7c055db46155b26a");  // md5(12345678)
    //         user.setAvatarUrl(AvatarUrls[indexRandomAvatarUrls]);
    //         user.setEmail(RandomStringUtils.randomAlphanumeric(10));
    //         user.setPhone(RandomStringUtils.randomNumeric(11));
    //         user.setUserStatus(0);
    //         user.setUserRole(randomUserRole);
    //         user.setPlanetCode(RandomStringUtils.randomNumeric(8));

    //         userMapper.insert(user);
    //     }

    //     stopWatch.stop();
    //     System.out.println(stopWatch.getTotalTimeMillis());  //2179ms
    // }
    
}
