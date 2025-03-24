package com.datascope.domain.model.metadata;

import lombok.Data;

/** 列元数据 */
@Data
public class ColumnMetadata {
  /** 所属表 */
  private TableMetadata table;

  /** 列名 */
  private String name;

  /** 列类型 */
  private ColumnType type;

  /** 长度 */
  private Integer length;

  /** 精度 */
  private Integer precision;

  /** 是否可为空 */
  private boolean nullable;

  /** 是否为主键 */
  private boolean primaryKey;

  /** 列位置 */
  private Integer ordinalPosition;

  /** 默认值 */
  private String defaultValue;

  /** 列注释 */
  private String comment;

  /** 是否自增 */
  private boolean autoIncrement;
}
