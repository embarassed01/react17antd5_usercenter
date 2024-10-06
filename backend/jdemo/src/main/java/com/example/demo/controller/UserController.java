package com.example.demo.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.BaseResponse;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.ResultUtils;
import com.example.demo.constant.UserConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.User;
import com.example.demo.model.request.UserLoginRequest;
import com.example.demo.model.request.UserRegisterRequest;
import com.example.demo.service.UserService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
  
/**
 * 用户接口
 * //@CrossOrigin(origins = { "http://localhost:5173" }) 
 */
@RestController 
@RequestMapping("/user") 
@Slf4j
public class UserController {
    
    @Resource 
    private UserService userService;

    @Resource 
    private RedisTemplate<String, Object> redisTemplate;
    
    @PostMapping("/register") 
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // `@RequestBody`：userRegisterRequest能够和前端传来的参数对应上
        if (userRegisterRequest == null) {
            // return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        } 
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    // @CrossOrigin
    @PostMapping("/login") 
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.doLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current") 
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 根据用户id，从数据库去拿真实信息
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);  // 脱敏
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search") 
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/update") 
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 1.校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 外面做一次鉴权
        User loginUser = userService.getLoginUser(request);
        // 里面再做一次鉴权
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete") 
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 自动改造成逻辑删除！！
        //  在删除时自动转换为调用更新
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    @GetMapping("/search/tags") 
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 推荐api，搞个分页page
     * @param pageSize 每页大小
     * @param pageNum 第几页
     * @param request
     * @return
     */
    @GetMapping("/recommend") 
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        // 如果有缓存，直接读
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("yupao:user:recommend:%d", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        @SuppressWarnings("unchecked")
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);

        if (userPage != null) {
            return ResultUtils.success(userPage);
        }

        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        // 写缓存  捕获异常，不影响正常查询
        try {
            valueOperations.set(redisKey, userPage, 100000, TimeUnit.MILLISECONDS);  // 设置过期时间（必需的）
        } catch (Exception e) {
            log.error("redis set key error", e);
        }

        return ResultUtils.success(userPage);
    }

}
