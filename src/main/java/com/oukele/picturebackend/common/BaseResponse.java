package com.oukele.picturebackend.common;

import com.oukele.picturebackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 全局数据返回封装类
 *
 * @author oukele
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
        this.message = "";
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage(), null);
    }

}
