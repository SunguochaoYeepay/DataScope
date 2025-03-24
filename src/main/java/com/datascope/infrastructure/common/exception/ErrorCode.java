package com.datascope.infrastructure.common.exception;

/**
 * 系统错误码枚举
 */
public enum ErrorCode {
    // 通用错误码 1000-1999
    SUCCESS(1000, "操作成功"),
    INTERNAL_ERROR(1001, "系统内部错误"),
    INVALID_PARAMETER(1002, "参数无效"),
    RESOURCE_NOT_FOUND(1003, "资源不存在"),
    DUPLICATE_NAME(1004, "名称重复"),
    OPERATION_FAILED(1005, "操作失败"),
    
    // 数据源错误码 2000-2999
    DATASOURCE_CONNECTION_FAILED(2000, "数据源连接失败"),
    DATASOURCE_INVALID_CONFIG(2001, "数据源配置无效"),
    DATASOURCE_ALREADY_EXISTS(2002, "数据源已存在"),
    DATASOURCE_NOT_FOUND(2003, "数据源不存在"),
    
    // 查询错误码 3000-3999
    QUERY_EXECUTION_FAILED(3000, "查询执行失败"),
    QUERY_TIMEOUT(3001, "查询超时"),
    QUERY_INVALID_SQL(3002, "无效的SQL"),
    QUERY_PERMISSION_DENIED(3003, "没有执行查询的权限"),
    
    // 元数据错误码 4000-4999
    METADATA_EXTRACTION_FAILED(4000, "元数据提取失败"),
    METADATA_SYNC_FAILED(4001, "元数据同步失败"),
    METADATA_NOT_FOUND(4002, "元数据不存在"),
    
    // 导出错误码 5000-5999
    EXPORT_EXECUTION_FAILED(5000, "导出执行失败"),
    EXPORT_CONFIG_NOT_FOUND(5001, "导出配置不存在");
    
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