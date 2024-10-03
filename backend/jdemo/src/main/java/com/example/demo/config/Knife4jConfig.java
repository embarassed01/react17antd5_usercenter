package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * 设置接口文档首页
 * springboot3.3只支持openapi3，之前的openapi2无法兼容！！
 */
@Configuration
public class Knife4jConfig {
    @Bean 
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
            .info(
                new Info().title("鱼皮用户中心API接口文档")  // 接口文档标题
                .description("鱼皮用户中心接口文档")  // 接口文档简介
                .version("0.0.1-SNAPSHOT")  // 接口文档版本
                .contact(new Contact().name("yupi").url("https://github.com/embarassed01").email("17616700589@163.com"))  // 开发者联系方式
            )
            .externalDocs(
                new ExternalDocumentation()
                .description("鱼皮用户中心接口文档")
                .url("http://127.0.0.1:8080")
            );
    }
}
