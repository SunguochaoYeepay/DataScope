package com.datascope.domain.model.metadata;

import com.datascope.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 表关系实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Relationship extends BaseEntity {
    /**
     * 源表ID
     */
    private String sourceTableId;
    
    /**
     * 源列ID
     */
    private String sourceColumnId;
    
    /**
     * 目标表ID
     */
    private String targetTableId;
    
    /**
     * 目标列ID
     */
    private String targetColumnId;
    
    /**
     * 关系类型（FOREIGN_KEY, INFERRED, MANUAL）
     */
    private String type;
    
    /**
     * 关系名称
     */
    private String name;
    
    /**
     * 置信度（0-100）
     */
    private BigDecimal confidence;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 是否已确认
     */
    private Boolean confirmed;
}