package com.datascope.domain.model.metadata;

import com.datascope.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据列实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Column extends BaseEntity {
    /**
     * 表ID
     */
    private String tableId;
    
    /**
     * 列名
     */
    private String name;
    
    /**
     * 数据类型
     */
    private String type;
    
    /**
     * 长度
     */
    private Integer length;
    
    /**
     * 精度
     */
    private Integer precision;
    
    /**
     * 小数位数
     */
    private Integer scale;
    
    /**
     * 是否可为空
     */
    private Boolean nullable;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 列注释
     */
    private String comment;
    
    /**
     * 是否主键
     */
    private Boolean isPrimaryKey;
    
    /**
     * 是否自增
     */
    private Boolean isAutoIncrement;
    
    /**
     * 字符集
     */
    private String charset;
    
    /**
     * 排序规则
     */
    private String collation;
    
    /**
     * 序号
     */
    private Integer ordinalPosition;
}