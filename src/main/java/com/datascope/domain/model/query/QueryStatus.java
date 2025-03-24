package com.datascope.domain.model.query;

/**
 * 查询状态枚举
 */
public enum QueryStatus {
    /**
     * 运行中
     */
    RUNNING,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 失败
     */
    FAILED,
    
    /**
     * 超时
     */
    TIMEOUT
}