package com.datascope.domain.model.export;

/**
 * 导出状态枚举
 */
public enum ExportStatus {
    /**
     * 等待中
     */
    PENDING,
    
    /**
     * 导出中
     */
    PROCESSING,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 失败
     */
    FAILED,
    
    /**
     * 已取消
     */
    CANCELLED
}