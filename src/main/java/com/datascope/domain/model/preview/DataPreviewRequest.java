package com.datascope.domain.model.preview;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DataPreviewRequest {
  /** 数据源ID */
  @NotNull(message = "数据源ID不能为空")
  @NotEmpty(message = "数据源ID不能为空")
  private String dataSourceId;

  /** 模式名 */
  @NotNull(message = "Schema不能为空")
  @NotEmpty(message = "Schema不能为空")
  private String schema;

  /** 表名 */
  @NotNull(message = "表名不能为空")
  @NotEmpty(message = "表名不能为空")
  private String tableName;

  /** 采样行数 */
  private Integer sampleSize = 100;

  /** 是否包含系统列 */
  private boolean includeSystemColumns = false;

  /** 排序字段 */
  private String orderBy;

  /** 是否降序 */
  private boolean desc = false;

  /** 过滤条件 */
  private String whereClause;
}
