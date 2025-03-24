package com.datascope.domain.model.preview;

import com.datascope.domain.model.metadata.ColumnMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class DataPreviewResponse {
  /** 列信息 */
  private List<ColumnMetadata> columns = new ArrayList<>();

  /** 数据行 每行数据是一个Map，key为列名，value为列值 */
  private List<Map<String, Object>> rows = new ArrayList<>();

  /** 总行数 */
  private Long totalRows;

  /** 采样行数 */
  private Integer sampleSize;

  /** 执行时间(毫秒) */
  private Long executionTime;
}
