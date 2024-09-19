package com.example.demo.common;

import java.io.Serializable;

import lombok.Data;

/**
 * 通用返回类, 泛型
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 4L;

    private int code;
    
    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

}
