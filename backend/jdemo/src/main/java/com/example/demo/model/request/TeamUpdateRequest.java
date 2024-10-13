package com.example.demo.model.request;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;

@Data
public class TeamUpdateRequest implements Serializable {
    
    private static final long serialVersionUID=30L;
    
    private Long id;
    
    private String name;

    private String description;

    private Date expireTime;
    
    private Integer status;

    private String password;
}
