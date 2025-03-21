package com.datascope.common.exception;

/**
 * 错误码枚举
 */
public enum ErrorCode {
    // 通用错误码
    SYSTEM_ERROR(10000, "系统错误"),
    PARAM_ERROR(10001, "参数错误"),
    
    // 数据源相关错误码 (11000-11999)
    DATASOURCE_NOT_FOUND(11000, "数据源不存在"),
    DATASOURCE_NAME_ALREADY_EXISTS(11001, "数据源名称已存在"),
    DATASOURCE_CONNECTION_FAILED(11002, "数据源连接失败"),
    UNSUPPORTED_DATASOURCE_TYPE(11003, "不支持的数据源类型"),
    
    // SQL解析相关错误码 (12000-12999)
    SQL_PARSE_ERROR(12000, "SQL解析错误"),
    SQL_VALIDATION_ERROR(12001, "SQL校验错误"),
    
    // 查询历史相关错误码 (13000-13999)
    QUERY_HISTORY_NOT_FOUND(13000, "查询历史不存在"),
    QUERY_EXECUTION_ERROR(13001, "查询执行错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}