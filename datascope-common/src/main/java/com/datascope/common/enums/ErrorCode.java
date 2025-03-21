package com.datascope.common.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    SUCCESS("200", "成功"),
    SYSTEM_ERROR("500", "系统错误"),
    PARAM_ERROR("400", "参数错误"),
    NOT_FOUND("404", "资源不存在"),
    UNAUTHORIZED("401", "未授权"),
    FORBIDDEN("403", "禁止访问"),
    
    // 数据源相关错误码 1000-1999
    DATASOURCE_NOT_FOUND("1000", "数据源不存在"),
    DATASOURCE_CONNECT_ERROR("1001", "数据源连接失败"),
    DATASOURCE_TYPE_NOT_SUPPORT("1002", "不支持的数据源类型"),
    
    // 元数据相关错误码 2000-2999
    METADATA_SYNC_ERROR("2000", "元数据同步失败"),
    METADATA_NOT_FOUND("2001", "元数据不存在"),
    
    // 查询相关错误码 3000-3999
    QUERY_TIMEOUT("3000", "查询超时"),
    QUERY_SYNTAX_ERROR("3001", "查询语法错误"),
    QUERY_EXECUTE_ERROR("3002", "查询执行失败"),
    QUERY_RESULT_TOO_LARGE("3003", "查询结果集过大"),
    
    // 配置相关错误码 4000-4999
    CONFIG_NOT_FOUND("4000", "配置不存在"),
    CONFIG_INVALID("4001", "配置无效"),
    
    // 限流相关错误码 5000-5999
    RATE_LIMIT_EXCEED("5000", "访问频率超限"),
    
    // AI相关错误码 6000-6999
    AI_SERVICE_ERROR("6000", "AI服务异常"),
    NL_TO_SQL_ERROR("6001", "自然语言转SQL失败");
    
    private final String code;
    private final String message;
}