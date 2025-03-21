package com.datascope.common.model;

import lombok.Data;
import java.util.List;

/**
 * 分页响应
 */
@Data
public class PageResponse<T> {
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 当前页码
     */
    private int pageNum;
    
    /**
     * 每页大小
     */
    private int pageSize;
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 创建分页响应
     */
    public static <T> PageResponse<T> of(List<T> list, long total, PageRequest request) {
        PageResponse<T> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(total);
        response.setPageNum(request.getPageNum());
        response.setPageSize(request.getPageSize());
        return response;
    }
}