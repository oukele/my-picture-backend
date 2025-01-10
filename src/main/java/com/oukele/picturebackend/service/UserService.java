package com.oukele.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.oukele.picturebackend.model.dto.user.UserJoinVipRequest;
import com.oukele.picturebackend.model.dto.user.UserQueryRequest;
import com.oukele.picturebackend.model.entity.User;
import com.oukele.picturebackend.model.vo.LoginUserVO;
import com.oukele.picturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author oukele
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-12-25 00:01:05
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @param shareCode     邀请码
     * @return 新用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String shareCode);

    /**
     * 获取加盐后的密码
     *
     * @param userPassword 用户密码
     * @return 加盐后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);


    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 用户通用查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户加入会员
     *
     * @param userJoinVipRequest 用户加入会员请求类
     * @return
     */
    boolean userJoinVip(UserJoinVipRequest userJoinVipRequest);

    /**
     * 生成用户的分享码
     *
     * @param request
     * @return 用的分享码
     */
    String getUserShareCode(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

}
