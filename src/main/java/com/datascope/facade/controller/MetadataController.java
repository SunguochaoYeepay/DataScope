package com.datascope.facade.controller;

import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.domain.service.metadata.MetadataStorageService;
import com.datascope.infrastructure.common.response.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 元数据管理接口
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/metadata")
public class MetadataController {

    private final MetadataStorageService metadataStorageService;

    /**
     * 获取数据源的所有表元数据
     *
     * @param datasourceId 数据源ID
     * @return 表元数据列表
     */
    @GetMapping("/datasources/{datasourceId}/tables")
    public R<List<TableMetadata>> listTables(@PathVariable("datasourceId") @NotNull Long datasourceId) {
        return R.ok(metadataStorageService.listTables(datasourceId));
    }

    /**
     * 获取指定表的元数据
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @return 表元数据
     */
    @GetMapping("/datasources/{datasourceId}/tables/{tableName}")
    public R<TableMetadata> getTable(@PathVariable("datasourceId") @NotNull Long datasourceId,
                                   @PathVariable("tableName") @NotBlank String tableName) {
        return R.ok(metadataStorageService.getTable(datasourceId, tableName));
    }

    /**
     * 刷新数据源的元数据
     *
     * @param datasourceId 数据源ID
     * @return 操作结果
     */
    @PostMapping("/datasources/{datasourceId}/refresh")
    public R<Void> refreshMetadata(@PathVariable("datasourceId") @NotNull Long datasourceId) {
        metadataStorageService.refreshMetadata(datasourceId);
        return R.ok();
    }

    /**
     * 刷新指定表的元数据
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @return 操作结果
     */
    @PostMapping("/datasources/{datasourceId}/tables/{tableName}/refresh")
    public R<Void> refreshTableMetadata(@PathVariable("datasourceId") @NotNull Long datasourceId,
                                      @PathVariable("tableName") @NotBlank String tableName) {
        metadataStorageService.refreshTableMetadata(datasourceId, tableName);
        return R.ok();
    }
}