package com.example.demo.model.vo;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;

/**
 * 用户包装类（脱敏）
 */
@Data
public class UserVO {
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;
    
    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     * 0 - 正常
     */
    private Integer userStatus;

    /**
     * 角色 0-普通用户 1-管理员
     */
    private Integer userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /** 
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签jsons
     */
    private String tags;

    /**
     * 详情描述
     */
    private String profile;

    /**
     * 星球编号
     */
    private String planetCode;
}
