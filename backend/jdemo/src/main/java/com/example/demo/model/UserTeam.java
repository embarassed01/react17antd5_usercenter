package com.example.demo.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 用户队伍关系表
 */
@TableName(value = "user_team")
@Data
public class UserTeam implements Serializable {
    
    @TableId(type = IdType.AUTO) 
    private Long id;

    private Long userId;

    private Long teamId;

    private Date joinTime;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 21L;
}
