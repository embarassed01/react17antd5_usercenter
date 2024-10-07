package com.example.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Redisson配置
 */
@Configuration  
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    private String database;

    @Bean 
    public RedissonClient redissonClient() {
        // 1.create config object
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        config.useSingleServer()
            // use "rediss://" for SSL connection
            .setAddress(redisAddress)
            .setPassword(password)
            .setDatabase(Integer.parseInt(database));
        // 2.create Redisson instance
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
