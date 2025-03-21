package com.datascope.domain.model.metadata;

import com.datascope.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Table extends BaseEntity {
    /**
     * 模式ID
     */
    private String schemaId;
    
    /**
     * 表名
     */
    private String name;
    
    /**
     * 表类型（TABLE, VIEW）
     */
    private String type;
    
    /**
     * 存储引擎
     */
    private String engine;
    
    /**
     * 表注释
     */
    private String comment;
    
    /**
     * 预估行数
     */
    private Long estimatedRows;
    
    /**
     * 数据长度（字节）
     */
    private Long dataLength;
    
    /**
     * 索引长度（字节）
     */
    private Long indexLength;
}