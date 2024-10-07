package com.example.demo.model.dto;

import com.example.demo.common.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 队伍查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    private Long id;

    private String name;

    private String description;

    private Integer maxNum;

    private Long userId;

    /**
     * 0-公开；1-隐私；2-加密
     */
    private Integer status;
}
