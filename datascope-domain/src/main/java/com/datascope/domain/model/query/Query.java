package com.datascope.domain.model.query;

import com.datascope.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Query extends BaseEntity {
    /**
     * 查询名称
     */
    private String name;
    
    /**
     * 数据源ID
     */
    private String sourceId;
    
    /**
     * SQL文本
     */
    private String sqlText;
    
    /**
     * 自然语言描述
     */
    private String description;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 状态（DRAFT, PUBLISHED, DEPRECATED）
     */
    private String status;
    
    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;
    
    /**
     * 是否收藏
     */
    private Boolean favorite;
    
    /**
     * 标签
     */
    private String tags;
    
    /**
     * 备注
     */
    private String remark;
}