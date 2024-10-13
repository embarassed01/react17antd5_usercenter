package com.example.demo.service.impl;

import java.lang.StackWalker.Option;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.ErrorCode;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.UserTeamMapper;
import com.example.demo.model.Team;
import com.example.demo.model.User;
import com.example.demo.model.UserTeam;
import com.example.demo.model.enums.TeamStatusEnum;
import com.example.demo.service.UserTeamService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam> implements UserTeamService {
    @Resource 
    private UserTeamMapper userTeamMapper;

}
