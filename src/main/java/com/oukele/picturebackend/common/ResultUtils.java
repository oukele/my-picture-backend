package com.oukele.picturebackend.common;

import com.oukele.picturebackend.exception.ErrorCode;

/**
 * 响应工具类
 *
 * @author oukele
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "ok", data);
    }

    /**
     * 失败
     *
     * @param errorCode 错误枚举
     * @return 响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }


    /**
     * 失败
     *
     * @param code    状态码
     * @param message 描述信息
     * @return 响应
     */
    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(code, message, null);
    }

    /**
     * 失败
     *
     * @param errorCode 错误枚举
     * @param message   描述信息
     * @return 响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), message, null);
    }

}
