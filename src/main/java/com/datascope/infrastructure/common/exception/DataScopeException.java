package com.datascope.infrastructure.common.exception;

import lombok.Getter;

/**
 * DataScope应用程序自定义异常
 * 用于表示应用程序内部的业务逻辑错误
 */
@Getter
public class DataScopeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final ErrorCode errorCode;

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public DataScopeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message 自定义错误消息
     */
    public DataScopeException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param cause 原始异常
     */
    public DataScopeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message 自定义错误消息
     * @param cause 原始异常
     */
    public DataScopeException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}