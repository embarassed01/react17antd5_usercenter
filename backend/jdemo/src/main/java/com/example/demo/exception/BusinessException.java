package com.example.demo.exception;

import com.example.demo.common.ErrorCode;

import lombok.Data;

/**
 * 自定义异常类
 * 继承运行时异常，就需要try-catch捕获异常类了
 */
@Data 
public class BusinessException extends RuntimeException {
    
    private int code;

    private String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
}
