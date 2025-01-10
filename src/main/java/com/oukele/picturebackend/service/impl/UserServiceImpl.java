package com.oukele.picturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oukele.picturebackend.constant.UserConstant;
import com.oukele.picturebackend.exception.BusinessException;
import com.oukele.picturebackend.exception.ErrorCode;
import com.oukele.picturebackend.exception.ThrowUtils;
import com.oukele.picturebackend.mapper.UserMapper;
import com.oukele.picturebackend.model.dto.user.UserJoinVipRequest;
import com.oukele.picturebackend.model.dto.user.UserQueryRequest;
import com.oukele.picturebackend.model.entity.User;
import com.oukele.picturebackend.model.enums.UserRoleEnum;
import com.oukele.picturebackend.model.vo.LoginUserVO;
import com.oukele.picturebackend.model.vo.UserVO;
import com.oukele.picturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oukele
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-12-25 00:01:05
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @param shareCode     邀请码
     * @return 新用户ID
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String shareCode) {
        // 1.参数校验
        if (StrUtil.hasEmpty(userAccount, userAccount, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2.检查用户账号是否在数据库中已有重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 3.用户密码进行加盐
        String encryptPassword = getEncryptPassword(userPassword);
        // 4.此用户数据插入到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserName("无名");
        user.setUserPassword(encryptPassword);
        user.setUserRole(UserRoleEnum.USER.getValue());
        // 5.是否有邀请码
        if (StrUtil.isNotBlank(shareCode)) {
            // 邀请码转换为真实的用户ID
            Long shareUserId = Long.parseLong(shareCode.substring(1));
            User shareUser = this.getById(shareUserId);
            ThrowUtils.throwIf(ObjUtil.isNull(shareUser), ErrorCode.OPERATION_ERROR, "无效的邀请码");
            user.setInviteUser(shareUser.getId());
        }
        ThrowUtils.throwIf(!this.save(user), ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        return user.getId();
    }

    /**
     * 获取加盐后的密码
     *
     * @param userPassword 用户密码
     * @return 加盐后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "oukele";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.参数校验
        ThrowUtils.throwIf(StrUtil.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "账号不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "密码不能为空");
        // 2.用户的密码加盐
        String encryptPassword = getEncryptPassword(userPassword);
        // 3.查询数据库数据
        User user = this.getOne(Wrappers.lambdaQuery(User.class).eq(User::getUserAccount, userAccount).eq(User::getUserPassword, encryptPassword));
        ThrowUtils.throwIf(ObjUtil.isNull(user), ErrorCode.PARAMS_ERROR, "用户不存在或者账号密码错误");
        // 4.保存用户登录态数据
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (ObjUtil.isNotNull(user)) {
            LoginUserVO loginUserVO = new LoginUserVO();
            BeanUtil.copyProperties(user, loginUserVO);
            return loginUserVO;
        }
        return null;
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if (ObjUtil.isNull(user)) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1.判断是否已经登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(ObjUtil.isNull(userObj), ErrorCode.NOT_LOGIN_ERROR);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(ObjUtil.isNull(currentUser.getId()), ErrorCode.NOT_LOGIN_ERROR);
        // 数据保持最新
        currentUser = this.getById(currentUser.getId());
        ThrowUtils.throwIf(ObjUtil.isNull(currentUser), ErrorCode.NOT_LOGIN_ERROR);
        return currentUser;
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 1.判断是否已经登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(ObjUtil.isNull(userObj), ErrorCode.OPERATION_ERROR, "未登录");
        // 2.移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 用户通用查询条件
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 用户加入会员
     *
     * @param userJoinVipRequest 用户加入会员请求类
     * @return
     */
    @Override
    public boolean userJoinVip(UserJoinVipRequest userJoinVipRequest) {
        // UserJoinVipRequest 可以只接受vipCode，用户的其他信息从session中获取

        // 1.参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(userJoinVipRequest), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjUtil.isNull(userJoinVipRequest.getId()), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(userJoinVipRequest.getVipCode()), ErrorCode.PARAMS_ERROR);

        // 2.兑换码校验
        if (!"oukele".equals(userJoinVipRequest.getVipCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的兑换码");
        }

        // 3.用户数据修改
        User user = this.getById(userJoinVipRequest.getId());
        ThrowUtils.throwIf(ObjUtil.isNull(user), ErrorCode.OPERATION_ERROR, "当前用户不存在");
        ThrowUtils.throwIf(ObjUtil.isNull(user.getId()), ErrorCode.OPERATION_ERROR, "当前用户不存在");
        ThrowUtils.throwIf(UserConstant.ADMIN_ROLE.equals(user.getUserRole()), ErrorCode.OPERATION_ERROR, "当前账号所属角色暂不支持此操作");

        // 构建更新的条件
        LambdaUpdateWrapper<User> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(User::getId, user.getId());
        updateWrapper.eq(ObjUtil.isNotNull(user.getVipNumber()), User::getVipNumber, user.getVipNumber());
        updateWrapper.eq(StrUtil.isNotBlank(user.getUserRole()), User::getUserRole, user.getUserRole());

        // 3.1 用户已经是会员-> 有效期在原来的基础上增加
        DateTime newExpireTime;
        Date currentDate = new Date();
        int expireNumber = 360;

        // 已经是会员
        if (ObjUtil.isNotNull(user.getVipNumber())) {
            // 已经超过有效期
            if (currentDate.after(user.getVipExpireTime())) {
                // 当前时间为有效期进行增加天数
                newExpireTime = DateUtil.offsetDay(currentDate, expireNumber);
            } else {
                // 在原来的有效期基础上进行增加天数
                newExpireTime = DateUtil.offsetDay(user.getVipExpireTime(), expireNumber);
            }
        } else {
            newExpireTime = DateUtil.offsetDay(currentDate, expireNumber);
            updateWrapper.set(User::getVipNumber, getNewVipNumber());
            updateWrapper.set(User::getUserRole, UserConstant.VIP_ROLE);
        }
        updateWrapper.set(User::getVipExpireTime, newExpireTime);
        updateWrapper.set(User::getVipCode, userJoinVipRequest.getVipCode());
        updateWrapper.set(User::getUpdateTime, new Date());
        return this.update(updateWrapper);
    }

    /**
     * 从数据库用户表中取到最大的编号并进行+1
     *
     * @return 新的会员编号
     */
    public Long getNewVipNumber() {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(User::getVipNumber).isNotNull(User::getVipNumber);
        User user = this.getOne(queryWrapper);
        if (ObjUtil.isNotNull(user)) {
            return user.getVipNumber() + 1;
        }
        return 1L;
    }

    /**
     * 生成用户的分享码
     *
     * @param request
     * @return 用的分享码
     */
    @Override
    public String getUserShareCode(HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        User databaseUser = this.getById(loginUser.getId());
        if (StrUtil.isNotBlank(databaseUser.getShareCode())) {
            return databaseUser.getShareCode();
        }
        // 分享码为空，生成并存储到数据库中
        String shareCode = "O" + databaseUser.getId();
        LambdaUpdateWrapper<User> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(User::getId, databaseUser.getId());
        updateWrapper.set(User::getShareCode, shareCode);
        updateWrapper.set(User::getUpdateTime, new Date());
        this.update(updateWrapper);
        return shareCode;
    }

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

}




