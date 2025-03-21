package com.datascope.domain.model.metadata;

import com.datascope.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库模式实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Schema extends BaseEntity {
    /**
     * 数据源ID
     */
    private String sourceId;
    
    /**
     * 模式名称
     */
    private String name;
    
    /**
     * 字符集
     */
    private String charset;
    
    /**
     * 排序规则
     */
    private String collation;
    
    /**
     * 描述
     */
    private String description;
}