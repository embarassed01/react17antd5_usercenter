package com.example.demo;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute.Use;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.google.gson.Gson;

import jakarta.annotation.Resource;

@SpringBootTest
public class InsertUsersTest {
    @Resource 
    private UserService userService;

    // 自定义线程池，不使用Java默认线程池（forkJoin库）
    // 超过线程池队列最大数目后，默认是abort丢弃策略
    private ExecutorService executorService = new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000), new AbortPolicy());

    private final static int INSERT_NUM = 100000;  //10万   //10000000; // 1000万

    private static final String[] AvatarUrls = {  // 头像池：20个
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

    private static final String[] ProgramTAGS = {
        "Java", "Python", "C++", "Zig", "Nodejs"
    };

    private static final String[] GradeTAGS = {
        "高中","大一","大二","大三","毕业生","硕士","在职","离职","未就业"
    };

    private static final String[] loveTAGS = {
        "打游戏","看动画","看电影","读小说","学习","打牌","麻将","看小片"
    };

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final String NUMERIC_STRING = "0123456789";

    private static String generateRandomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder();
        SecureRandom sr = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = sr.nextInt(ALPHA_NUMERIC_STRING.length());
            sb.append(ALPHA_NUMERIC_STRING.charAt(index));
        }
        return sb.toString();
    }

    private static String generateRandomNumeric(int length) {
        StringBuilder sb = new StringBuilder();
        SecureRandom sr = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = sr.nextInt(NUMERIC_STRING.length());
            sb.append(NUMERIC_STRING.charAt(index));
        }
        return sb.toString();
    }

    @SuppressWarnings("deprecation") 
    private static List<User> generateRandomUsers(int totalCount) {
        List<User> userList = new ArrayList<>();  // 线程不安全
        for (int i = 0; i < totalCount; i++) {
            String randomAvatarUrl = AvatarUrls[(int) (Math.random() * AvatarUrls.length)];
            int randomGender = (int) (Math.random() * 2);
            int randomUserRole = (int) (Math.random() * 2);
            String randomUserAccount = generateRandomAlphanumeric(6);  // 唯一性
            StringBuilder sbRandomEmail = new StringBuilder();
            sbRandomEmail.append(RandomStringUtils.randomAlphanumeric(8));
            sbRandomEmail.append("@gmail.com");
            String randomEmail = sbRandomEmail.toString();
            String password = "b0dd3697a192885d7c055db46155b26a";  // md5(12345678)
            String randomPhone = RandomStringUtils.randomNumeric(11);
            String randomPlanetCode = generateRandomNumeric(6);  // 唯一性
            List<String> tagsList = new ArrayList<>();
            tagsList.add(randomGender == 0 ? "女" : "男");
            tagsList.add(ProgramTAGS[(int) (Math.random() * ProgramTAGS.length)]);
            tagsList.add(GradeTAGS[(int) (Math.random() * GradeTAGS.length)]);
            tagsList.add(loveTAGS[(int) (Math.random() * loveTAGS.length)]);
            Gson gson = new Gson();
            String randomTags = gson.toJson(tagsList);

            User user = new User();
            user.setUsername(randomUserAccount);
            user.setUserAccount(randomUserAccount);
            user.setGender(randomGender);
            user.setUserPassword(password);
            user.setAvatarUrl(randomAvatarUrl);
            user.setEmail(randomEmail);
            user.setPhone(randomPhone);
            user.setUserStatus(0);
            user.setUserRole(randomUserRole);
            user.setPlanetCode(randomPlanetCode);
            user.setTags(randomTags);

            // userMapper.insert(user);  // 只能单个插入
            userList.add(user);
        }
        return userList;
    }

    @SuppressWarnings("deprecation") 
    private static List<User> generateConcurrencyRandomUsers(int totalCount) {
        List<User> userList = Collections.synchronizedList(new ArrayList<>());  // 线程安全的
        for (int i = 0; i < totalCount; i++) {
            String randomAvatarUrl = AvatarUrls[(int) (Math.random() * AvatarUrls.length)];
            int randomGender = (int) (Math.random() * 2);
            int randomUserRole = (int) (Math.random() * 2);
            String randomUserAccount = generateRandomAlphanumeric(6);  // 唯一性
            StringBuilder sbRandomEmail = new StringBuilder();
            sbRandomEmail.append(RandomStringUtils.randomAlphanumeric(8));
            sbRandomEmail.append("@gmail.com");
            String randomEmail = sbRandomEmail.toString();
            String password = "b0dd3697a192885d7c055db46155b26a";  // md5(12345678)
            String randomPhone = RandomStringUtils.randomNumeric(11);
            String randomPlanetCode = generateRandomNumeric(6);  // 唯一性
            List<String> tagsList = new ArrayList<>();
            tagsList.add(randomGender == 0 ? "女" : "男");
            tagsList.add(ProgramTAGS[(int) (Math.random() * ProgramTAGS.length)]);
            tagsList.add(GradeTAGS[(int) (Math.random() * GradeTAGS.length)]);
            tagsList.add(loveTAGS[(int) (Math.random() * loveTAGS.length)]);
            Gson gson = new Gson();
            String randomTags = gson.toJson(tagsList);

            User user = new User();
            user.setUsername(randomUserAccount);
            user.setUserAccount(randomUserAccount);
            user.setGender(randomGender);
            user.setUserPassword(password);
            user.setAvatarUrl(randomAvatarUrl);
            user.setEmail(randomEmail);
            user.setPhone(randomPhone);
            user.setUserStatus(0);
            user.setUserRole(randomUserRole);
            user.setPlanetCode(randomPlanetCode);
            user.setTags(randomTags);

            // userMapper.insert(user);  // 只能单个插入
            userList.add(user);
        }
        return userList;
    }

    /**
     * 批量插入用户, 模拟
     * @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE) 单次任务
     */
    @Test 
    @SuppressWarnings("deprecation")
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<User> userList = generateRandomUsers(INSERT_NUM);
        userService.saveBatch(userList, 100);

        stopWatch.stop();
        System.out.printf("总耗时间（毫秒）：%s\n", stopWatch.getTotalTimeMillis());  //1.7s
    }

    /**
     * 并发批量插入数据
     */
    @Test 
    @SuppressWarnings("deprecation")
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 分十组
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<User> userList = generateConcurrencyRandomUsers(INSERT_NUM / 10);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, INSERT_NUM / 10);
            }, executorService);
            futureList.add(future);  // 拿到10个异步任务
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();  // join() 阻塞一下

        stopWatch.stop();
        System.out.printf("总耗时间（毫秒）：%s\n", stopWatch.getTotalTimeMillis());  //3.2s  // 24s
    }

}
