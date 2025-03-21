package com.datascope.domain.model.query;

import com.datascope.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询历史实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryHistory extends BaseEntity {
    /**
     * 查询ID
     */
    private String queryId;
    
    /**
     * 查询参数（JSON格式）
     */
    private String parameters;
    
    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;
    
    /**
     * 影响行数
     */
    private Long affectedRows;
    
    /**
     * 状态（SUCCESS, FAILED）
     */
    private String status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 执行IP
     */
    private String executionIp;
}