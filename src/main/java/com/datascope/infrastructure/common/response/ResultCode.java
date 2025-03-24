package com.datascope.infrastructure.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 响应码枚举 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {
  SUCCESS(200, "操作成功"),
  UNAUTHORIZED(401, "未授权"),
  FORBIDDEN(403, "无权限"),
  NOT_FOUND(404, "资源不存在"),
  METHOD_NOT_ALLOWED(405, "请求方法不允许"),
  PARAM_ERROR(400, "参数错误"),
  SYSTEM_ERROR(500, "系统错误"),
  SERVICE_ERROR(503, "服务不可用"),

  // 数据源相关错误码
  DATASOURCE_CONNECTION_ERROR(1001, "数据源连接失败"),
  DATASOURCE_NOT_FOUND(1002, "数据源不存在"),
  DATASOURCE_TYPE_NOT_SUPPORTED(1003, "不支持的数据源类型"),
  DATASOURCE_CONFIG_ERROR(1004, "数据源配置错误"),

  // 元数据相关错误码
  METADATA_EXTRACT_ERROR(2001, "元数据提取失败"),
  METADATA_NOT_FOUND(2002, "元数据不存在"),
  METADATA_UPDATE_ERROR(2003, "元数据更新失败"),

  // 数据预览相关错误码
  PREVIEW_ERROR(3001, "数据预览失败"),
  PREVIEW_TIMEOUT(3002, "数据预览超时"),
  PREVIEW_PERMISSION_DENIED(3003, "无数据预览权限");

  /** 响应码 */
  private final Integer code;

  /** 响应消息 */
  private final String message;
}
