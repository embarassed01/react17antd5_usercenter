package com.example.demo.common;

import java.io.Serializable;

import lombok.Data;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {
    
    /**
     * 页面大小
     */
    protected int pageSize;

    /**
     * 当前是第几页
     */
    protected int pageNum;

    // 序列化UID，可以使对象在序列化的时候保持唯一！
    private static final long serialVersionUID = 22L;

}
