package com.datascope.common.response;

import lombok.Data;

/** 通用响应对象 */
@Data
public class R<T> {
  private Integer code;
  private String message;
  private T data;

  private R(Integer code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> R<T> ok() {
    return new R<>(200, "success", null);
  }

  public static <T> R<T> ok(T data) {
    return new R<>(200, "success", data);
  }

  public static <T> R<T> ok(String message, T data) {
    return new R<>(200, message, data);
  }

  public static <T> R<T> error(Integer code, String message) {
    return new R<>(code, message, null);
  }

  public static <T> R<T> error(String message) {
    return new R<>(500, message, null);
  }
}
