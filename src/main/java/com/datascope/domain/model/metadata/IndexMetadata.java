package com.datascope.domain.model.metadata;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 索引元数据
 */
@Data
public class IndexMetadata {
    /**
     * 所属表
     */
    private TableMetadata table;

    /**
     * 索引名
     */
    private String name;

    /**
     * 是否唯一索引
     */
    private boolean unique;

    /**
     * 基数
     */
    private Long cardinality;

    /**
     * 索引大小(字节)
     */
    private Long indexSize;

    /**
     * 索引列
     */
    private final List<String> columns = new ArrayList<>();

    public void addColumn(String column) {
        columns.add(column);
    }
}