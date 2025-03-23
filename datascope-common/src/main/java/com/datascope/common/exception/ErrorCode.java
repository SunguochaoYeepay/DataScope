package com.datascope.common.exception;

/**
 * 系统错误码定义
 * 
 * 错误码设计规范：
 * 1. 错误码长度为6位数字
 * 2. 前两位代表错误类型：
 *    - 40: 客户端错误
 *    - 41: 认证授权错误
 *    - 42: 业务逻辑错误
 *    - 50: 服务端错误
 * 3. 中间两位代表业务模块：
 *    - 00: 通用模块
 *    - 01: 数据源模块
 *    - 02: SQL解析模块
 *    - 03: 查询历史模块
 * 4. 最后两位代表具体错误
 */
public enum ErrorCode {
    
    // ========== 通用错误码 [400000-400099] ==========
    /**
     * 参数验证失败
     */
    INVALID_PARAMETER(400001, "参数验证失败"),
    
    /**
     * 分页参数无效
     */
    INVALID_PAGE_PARAMETER(400002, "分页参数无效"),
    
    /**
     * 排序参数无效
     */
    INVALID_SORT_PARAMETER(400003, "排序参数无效"),
    
    /**
     * 状态值无效
     */
    INVALID_STATUS(400004, "状态值无效"),
    
    /**
     * 资源不存在
     */
    RESOURCE_NOT_FOUND(404001, "资源不存在"),
    
    /**
     * 资源已存在
     */
    RESOURCE_ALREADY_EXISTS(409001, "资源已存在"),
    
    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(500000, "系统内部错误"),
    
    // ========== 数据源模块错误码 [420100-420199] ==========
    /**
     * 数据源不存在
     */
    DATASOURCE_NOT_FOUND(420101, "数据源不存在"),
    
    /**
     * 数据源名称已存在
     */
    DATASOURCE_NAME_EXISTS(420102, "数据源名称已存在"),
    
    /**
     * 数据源类型不支持
     */
    DATASOURCE_TYPE_NOT_SUPPORTED(420103, "不支持的数据源类型"),
    
    /**
     * 数据源连接失败
     */
    DATASOURCE_CONNECTION_FAILED(420104, "数据源连接失败"),
    
    /**
     * 数据源配置无效
     */
    DATASOURCE_CONFIG_INVALID(420105, "数据源配置无效"),
    
    /**
     * 数据源已被使用
     */
    DATASOURCE_IN_USE(420106, "数据源已被使用，无法删除"),
    
    // ========== SQL解析模块错误码 [420200-420299] ==========
    /**
     * SQL语法错误
     */
    SQL_SYNTAX_ERROR(420201, "SQL语法错误"),
    
    /**
     * SQL执行超时
     */
    SQL_EXECUTION_TIMEOUT(420202, "SQL执行超时"),
    
    /**
     * SQL权限不足
     */
    SQL_PERMISSION_DENIED(420203, "SQL执行权限不足"),
    
    // ========== 查询历史模块错误码 [420300-420399] ==========
    /**
     * 查询历史不存在
     */
    QUERY_HISTORY_NOT_FOUND(420301, "查询历史不存在"),
    
    /**
     * 查询历史已存在
     */
    QUERY_HISTORY_EXISTS(420302, "查询历史已存在");

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