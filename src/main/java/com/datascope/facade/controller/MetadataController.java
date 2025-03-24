package com.datascope.facade.controller;

import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.domain.service.metadata.MetadataFetchService;
import com.datascope.domain.service.metadata.MetadataStorageService;
import com.datascope.infrastructure.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 元数据管理接口 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/metadata")
@Tag(name = "元数据管理", description = "数据库元数据的查询和管理")
public class MetadataController {

  private final MetadataStorageService metadataStorageService;
  private final MetadataFetchService metadataFetchService;

  /**
   * 获取数据源的所有表元数据
   *
   * @param datasourceId 数据源ID
   * @return 表元数据列表
   */
  @GetMapping("/datasources/{datasourceId}/tables")
  @Operation(summary = "获取数据源的所有表", description = "获取指定数据源下的所有表元数据信息")
  public R<List<TableMetadata>> listTables(
      @PathVariable("datasourceId") @NotNull String datasourceId) {
    return R.ok(metadataStorageService.getTableMetadataByDataSource(datasourceId));
  }

  /**
   * 获取指定表的元数据
   *
   * @param datasourceId 数据源ID
   * @param schema schema名称
   * @param tableName 表名
   * @return 表元数据
   */
  @GetMapping("/datasources/{datasourceId}/schemas/{schema}/tables/{tableName}")
  @Operation(summary = "获取指定表的元数据", description = "获取特定数据源、特定schema下的表元数据详情")
  public R<TableMetadata> getTable(
      @PathVariable("datasourceId") @NotNull String datasourceId,
      @PathVariable("schema") @NotBlank String schema,
      @PathVariable("tableName") @NotBlank String tableName) {
    return R.ok(metadataStorageService.getTableMetadata(datasourceId, schema, tableName));
  }

  /**
   * 刷新数据源的元数据
   *
   * @param datasourceId 数据源ID
   * @return 刷新结果
   */
  @PostMapping("/datasources/{datasourceId}/refresh")
  @Operation(summary = "刷新数据源元数据", description = "重新加载指定数据源的所有元数据信息")
  public R<Void> refreshMetadata(@PathVariable("datasourceId") @NotNull String datasourceId) {
    // 先删除现有元数据
    metadataStorageService.deleteByDataSource(datasourceId);
    // 重新获取并保存元数据
    metadataFetchService.fetchAndSaveAllMetadata(datasourceId);
    return R.ok();
  }

  /**
   * 刷新指定表的元数据
   *
   * @param datasourceId 数据源ID
   * @param schema schema名称
   * @param tableName 表名
   * @return 刷新结果
   */
  @PostMapping("/datasources/{datasourceId}/schemas/{schema}/tables/{tableName}/refresh")
  @Operation(summary = "刷新指定表的元数据", description = "重新加载特定表的元数据信息")
  public R<Void> refreshTableMetadata(
      @PathVariable("datasourceId") @NotNull String datasourceId,
      @PathVariable("schema") @NotBlank String schema,
      @PathVariable("tableName") @NotBlank String tableName) {
    
    // 获取并保存新的表元数据
    metadataFetchService.fetchAndSaveTableMetadata(datasourceId, schema, tableName);
    return R.ok();
  }
}
