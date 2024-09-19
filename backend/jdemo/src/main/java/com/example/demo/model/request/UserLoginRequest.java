package com.example.demo.model.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户登录请求体
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 3L;

    private String userAccount;

    private String userPassword;
}
