package com.datascope.common.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 基础实体类
 */
@Data
public class BaseEntity {
    /**
     * 主键ID
     */
    private String id;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 更新人
     */
    private String updatedBy;
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
}