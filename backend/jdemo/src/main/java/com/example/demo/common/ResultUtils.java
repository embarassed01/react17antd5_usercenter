package com.example.demo.common;

/**
 * 返回工具类
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(0, data, "ok");
    }
}
