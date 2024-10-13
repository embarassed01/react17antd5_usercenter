package com.example.demo.model.request;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;

@Data
public class TeamAddRequest implements Serializable {
    
    private static final long serialVersionUID=23L;
    
    private String name;

    private String description;

    private Integer maxNum;

    private Date expireTime;

    private Long userId;
    
    private Integer status;

    private String password;
}
