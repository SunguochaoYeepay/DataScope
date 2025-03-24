package com.datascope.common.exception;

/**
 * DataScope应用程序自定义异常
 * 用于表示应用程序内部的业务逻辑错误
 */
public class DataScopeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * 构造函数
   *
   * @param message 异常消息
   */
  public DataScopeException(String message) {
    super(message);
  }

  /**
   * 构造函数
   *
   * @param message 异常消息
   * @param cause 原始异常
   */
  public DataScopeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * 构造函数
   *
   * @param cause 原始异常
   */
  public DataScopeException(Throwable cause) {
    super(cause);
  }
}
