package com.example.demo.model.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class TeamQuitRequest implements Serializable {
    
    private static final long serialVersionUID=32L;
    
    private Long teamId;
}
