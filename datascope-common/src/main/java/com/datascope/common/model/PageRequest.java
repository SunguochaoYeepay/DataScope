package com.datascope.common.model;

import lombok.Data;

/**
 * 分页请求
 */
@Data
public class PageRequest {
    /**
     * 页码
     */
    private int pageNum = 1;
    
    /**
     * 每页大小
     */
    private int pageSize = 10;
    
    /**
     * 排序字段
     */
    private String orderBy;
    
    /**
     * 排序方向
     */
    private String orderDirection;
}