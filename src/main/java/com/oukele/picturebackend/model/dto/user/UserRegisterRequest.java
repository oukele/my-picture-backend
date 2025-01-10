package com.oukele.picturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 *
 * @author oukele
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 9191048906274245221L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

    /**
     * 分享码
     */
    private String shareCode;
}
