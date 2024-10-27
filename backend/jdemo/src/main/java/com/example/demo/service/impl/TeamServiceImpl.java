package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.ErrorCode;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.TeamMapper;
import com.example.demo.model.Team;
import com.example.demo.model.User;
import com.example.demo.model.UserTeam;
import com.example.demo.model.dto.TeamQuery;
import com.example.demo.model.enums.TeamStatusEnum;
import com.example.demo.model.request.TeamJoinRequest;
import com.example.demo.model.request.TeamQuitRequest;
import com.example.demo.model.request.TeamUpdateRequest;
import com.example.demo.model.vo.TeamUserVO;
import com.example.demo.model.vo.UserVO;
import com.example.demo.service.TeamService;
import com.example.demo.service.UserService;
import com.example.demo.service.UserTeamService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
   
    @Resource
    private UserTeamService userTeamService;

    @Resource 
    private UserService userService;

    @Resource 
    private RedissonClient redissonClient;

    @Override 
    @Transactional(rollbackFor = Exception.class)  // 添加事务（原子操作sql）
    public long addTeam(Team team, User loginUser) {
        // 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final long userId = loginUser.getId();
        // 校验信息：队伍人数>1且<=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        // 校验：队伍标题名<=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // 校验：如果有描述，长度<=512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // status是否公开，默认不传为公开0
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        // 如果status是加密状态，一定要有密码，且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        // 超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间>当前时间");
        }
        // 校验用户最多创建5个队伍
        // TODO 有bug，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        // 插入队伍信息到队伍表
        // 同时，插入 用户=>队伍关系 到关系表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        /*if (true) {  // 说明 事务注解 是生效的，一旦异常就会回滚。上一步sql保存操作就会回滚。
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建用户关系失败");
        }*/
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        // -> 关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建用户关系失败");
        }
        return teamId;
    }

    @Override 
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (!CollectionUtils.isEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数相等的
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根据创建人来查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            // 根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                // 默认：只查公开的房间？加密的呢？？
                statusEnum = TeamStatusEnum.PUBLIC;
            }

            // TODO 私密的房间只有管理员才能查看（逻辑不是很好！！）
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);  // 无权限
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }

        // 不展示已过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }

        // 关联查询用户信息
        // 1.自己写SQL
        // 查询队伍和创建人的信息
        // select * from team t left join user u on t.userId = u.id
        // 查询队伍和已加入队伍成员的信息
        // select * from team t left join user_team ut on t.id = ut.teamId left join user u on ut.userId = u.id;
        // 这里只实现查询创建人信息，如果也要查询队伍成员信息，最好用sql语句关联查询快
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
                teamUserVOList.add(teamUserVO);
            } 
        }
        return teamUserVOList;
    }

    @Override 
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        Team oldTeam = this.getTeamById(id);
        // 只有管理员或队伍创建者可以修改
        if ((long) oldTeam.getUserId() != (long) loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        boolean result = this.updateById(updateTeam);
        return result;
    }

    /**
     * TODO bug 如果用户快速多次点击，还是会重复加入队伍的！！需要加🔒(分布式锁！！)
     */
    @Override 
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {  // 书写小技巧：将确定不为空的obj放在equals的前面
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (teamStatusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        
        Long userId = loginUser.getId();
        // 只有一个线程能获取到锁
        RLock lock = redissonClient.getLock("yupao:join_team");  // 粗暴一点，大家抢同一把锁（应该是区分不同队伍id）
        try {
            // 抢到锁 并 执行
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    System.out.println("getLock:" + Thread.currentThread().getId());
                    // 可以直接加个大锁，所有线程都排队
                    /*synchronized (this)*/ {
                    // 第一把锁
                    // /*synchronized (String.valueOf(userId).intern())*/ {  // .intern用于拿到字符串的常量，保证每次拿到的不是new的
                        // 该用户已加入的队伍数量
                        // 这个用户加入多少队伍
                        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                        userTeamQueryWrapper.eq("userId", userId);
                        long hasJoinTeam = userTeamService.count(userTeamQueryWrapper);
                        if (hasJoinTeam > 5) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入5个队伍");
                        }
                        // 不能重复加入已加入的队伍
                        userTeamQueryWrapper = new QueryWrapper<>();
                        userTeamQueryWrapper.eq("userId", userId);
                        userTeamQueryWrapper.eq("teamId", teamId);
                        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
                        if (hasUserJoinTeam > 0) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
                        }
                    // }

                    // 第二把锁 team
                    // /*synchronized ()*/ {
                        // 已加入队伍的人数 这个队伍加入多少用户
                        long teamHasJoinTeam = this.countTeamUserByTeamId(teamId);
                        if (teamHasJoinTeam >= team.getMaxNum()) {
                            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已满");
                        }
                        // 新增队伍-用户关联信息
                        UserTeam userTeam = new UserTeam();
                        userTeam.setUserId(userId);
                        userTeam.setTeamId(teamId);
                        userTeam.setJoinTime(new Date());
                        return userTeamService.save(userTeam);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCache error", e);
            return false;
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
   
    @Transactional(rollbackFor = Exception.class)
    @Override 
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        Long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        long teamHasJoinTeam = this.countTeamUserByTeamId(teamId);
        // 队伍只剩一人，解散
        if (teamHasJoinTeam == 1) {
            // 删除队伍
            this.removeById(teamId);
            // QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            // userTeamQueryWrapper.eq("teamId", teamId);
            // return userTeamService.remove(userTeamQueryWrapper);
        } else {
            // 是否为队长
            Long teamUesrId = team.getUserId();
            if (teamUesrId.equals(userId)) {
                // 把队伍转移给最早加入的用户  id自增的，id小的加入时间就早！
                // 1.查询已加入队伍的所有用户和加入时间 (最早的2条即可)
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");  // last表明会拼接到sql的最后
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        // 退出队伍，移除关联
        return userTeamService.remove(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override 
    public boolean deleteTeam(long id, User loginUser) {
        // 校验队伍是否存在
        Team team = this.getTeamById(id);
        long teamId = team.getId();
        // 校验是不是队伍的队长
        if ((long) team.getUserId() != (long) loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无访问权限");
        }
        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        return this.removeById(teamId);
    }

    /**
     * 获取某队伍当前人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        return userTeamService.count(queryWrapper);
    }

    /**
     * 根据id获取队伍信息
     * @param teamId
     * @return
     */
    public Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }
}
