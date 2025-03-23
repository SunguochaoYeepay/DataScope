package com.datascope.infrastructure.common.exception;

import com.datascope.infrastructure.common.response.IResultCode;
import lombok.Getter;

/**
 * 基础异常类
 */
@Getter
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final IResultCode resultCode;

    public BaseException(String message) {
        super(message);
        this.resultCode = null;
    }

    public BaseException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BaseException(IResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public BaseException(IResultCode resultCode, Throwable cause) {
        super(cause);
        this.resultCode = resultCode;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.resultCode = null;
    }
}