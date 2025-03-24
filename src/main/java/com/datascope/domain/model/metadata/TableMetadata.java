package com.datascope.domain.model.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/** 表元数据 */
@Data
public class TableMetadata {
  /** 数据源ID */
  private String dataSourceId;

  /** 模式名 */
  private String schema;

  /** 表名 */
  private String name;

  /** 表注释 */
  private String comment;

  /** 表行数 */
  private Long rowCount;

  /** 数据大小(字节) */
  private Long dataSize;

  /** 索引大小(字节) */
  private Long indexSize;

  /** 列信息 */
  private final List<ColumnMetadata> columns = new ArrayList<>();

  /** 索引信息 */
  private final List<IndexMetadata> indices = new ArrayList<>();

  /** 列名到列信息的映射 */
  private final Map<String, ColumnMetadata> columnMap = new HashMap<>();

  public void addColumn(ColumnMetadata column) {
    columns.add(column);
    columnMap.put(column.getName(), column);
    column.setTable(this);
  }

  public ColumnMetadata getColumn(String name) {
    return columnMap.get(name);
  }

  public void addIndex(IndexMetadata index) {
    indices.add(index);
    index.setTable(this);
  }
}
