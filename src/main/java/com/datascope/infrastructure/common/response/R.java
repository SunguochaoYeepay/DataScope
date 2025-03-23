package com.datascope.infrastructure.common.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一响应对象
 */
@Data
@Accessors(chain = true)
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求ID，用于追踪
     */
    private String requestId;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功响应
     */
    public static <T> R<T> ok() {
        return ok(null);
    }

    /**
     * 成功响应
     */
    public static <T> R<T> ok(T data) {
        return new R<T>()
                .setCode(ResultCode.SUCCESS.getCode())
                .setMessage(ResultCode.SUCCESS.getMessage())
                .setData(data)
                .setTimestamp(System.currentTimeMillis());
    }

    /**
     * 失败响应
     */
    public static <T> R<T> fail() {
        return fail(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 失败响应
     */
    public static <T> R<T> fail(String message) {
        return new R<T>()
                .setCode(ResultCode.SYSTEM_ERROR.getCode())
                .setMessage(message)
                .setTimestamp(System.currentTimeMillis());
    }

    /**
     * 失败响应
     */
    public static <T> R<T> fail(IResultCode resultCode) {
        return new R<T>()
                .setCode(resultCode.getCode())
                .setMessage(resultCode.getMessage())
                .setTimestamp(System.currentTimeMillis());
    }

    /**
     * 失败响应
     */
    public static <T> R<T> fail(IResultCode resultCode, String message) {
        return new R<T>()
                .setCode(resultCode.getCode())
                .setMessage(message)
                .setTimestamp(System.currentTimeMillis());
    }
}