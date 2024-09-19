package com.example.demo.model.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {
    
    private static final long serialVersionUID = 2L;  // 防止序列化过程中冲突，只要唯一、不一样就行

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;
}
