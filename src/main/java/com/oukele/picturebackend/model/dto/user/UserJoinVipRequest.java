package com.oukele.picturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入会员请求类
 *
 * @author oukele
 */
@Data
public class UserJoinVipRequest implements Serializable {

    private static final long serialVersionUID = 4063913074207806268L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 兑换码
     */
    private String vipCode;
}
