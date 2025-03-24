package com.datascope.infrastructure.common.exception;

import com.datascope.infrastructure.common.response.IResultCode;

/** 业务异常类 */
public class BusinessException extends BaseException {
  private static final long serialVersionUID = 1L;

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(IResultCode resultCode) {
    super(resultCode);
  }

  public BusinessException(IResultCode resultCode, String message) {
    super(resultCode, message);
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }

  public static BusinessException of(String message) {
    return new BusinessException(message);
  }

  public static BusinessException of(IResultCode resultCode) {
    return new BusinessException(resultCode);
  }

  public static BusinessException of(IResultCode resultCode, String message) {
    return new BusinessException(resultCode, message);
  }
}
