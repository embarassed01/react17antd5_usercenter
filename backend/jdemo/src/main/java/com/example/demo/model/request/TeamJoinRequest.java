package com.example.demo.model.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class TeamJoinRequest implements Serializable {
    
    private static final long serialVersionUID=31L;
    
    private Long teamId;

    private String password;
}
