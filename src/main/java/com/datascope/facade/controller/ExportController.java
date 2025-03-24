package com.datascope.facade.controller;

import com.datascope.domain.model.export.ExportConfigVO;
import com.datascope.domain.model.export.ExportHistoryVO;
import com.datascope.domain.model.export.ExportStatus;
import com.datascope.domain.service.export.ExportService;
import com.datascope.infrastructure.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 导出功能控制器
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/exports")
@Tag(name = "数据导出", description = "数据导出相关接口")
public class ExportController {

    private final ExportService exportService;

    /**
     * 创建导出配置
     *
     * @param exportConfig 导出配置信息
     * @return 创建的导出配置
     */
    @PostMapping("/configs")
    @Operation(summary = "创建导出配置", description = "创建新的导出配置")
    public R<ExportConfigVO> createExportConfig(@RequestBody @Valid ExportConfigVO exportConfig) {
        return R.ok(exportService.createExportConfig(exportConfig));
    }

    /**
     * 更新导出配置
     *
     * @param id           配置ID
     * @param exportConfig 导出配置信息
     * @return 更新后的导出配置
     */
    @PutMapping("/configs/{id}")
    @Operation(summary = "更新导出配置", description = "根据ID更新导出配置")
    public R<ExportConfigVO> updateExportConfig(
            @PathVariable String id, @RequestBody @Valid ExportConfigVO exportConfig) {
        return R.ok(exportService.updateExportConfig(id, exportConfig));
    }

    /**
     * 删除导出配置
     *
     * @param id 配置ID
     * @return 操作结果
     */
    @DeleteMapping("/configs/{id}")
    @Operation(summary = "删除导出配置", description = "根据ID删除导出配置")
    public R<Void> deleteExportConfig(@PathVariable String id) {
        exportService.deleteExportConfig(id);
        return R.ok();
    }

    /**
     * 获取导出配置详情
     *
     * @param id 配置ID
     * @return 导出配置详情
     */
    @GetMapping("/configs/{id}")
    @Operation(summary = "获取导出配置详情", description = "根据ID获取导出配置详情")
    public R<ExportConfigVO> getExportConfig(@PathVariable String id) {
        return R.ok(exportService.getExportConfig(id));
    }

    /**
     * 获取所有导出配置
     *
     * @return 导出配置列表
     */
    @GetMapping("/configs")
    @Operation(summary = "获取所有导出配置", description = "获取所有导出配置列表")
    public R<List<ExportConfigVO>> getAllExportConfigs() {
        return R.ok(exportService.getAllExportConfigs());
    }

    /**
     * 分页查询导出配置
     *
     * @param name         配置名称
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     * @param pageable     分页参数
     * @return 分页结果
     */
    @GetMapping("/configs/search")
    @Operation(summary = "分页查询导出配置", description = "根据条件分页查询导出配置")
    public R<Page<ExportConfigVO>> searchExportConfigs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dataSourceId,
            @RequestParam(required = false) String tableName,
            @PageableDefault(size = 10) Pageable pageable) {
        return R.ok(exportService.findExportConfigs(name, dataSourceId, tableName, pageable));
    }

    /**
     * 执行导出
     *
     * @param configId 配置ID
     * @return 导出任务信息
     */
    @PostMapping("/execute/{configId}")
    @Operation(summary = "执行导出", description = "根据配置ID执行数据导出")
    public R<ExportHistoryVO> executeExport(@PathVariable String configId) {
        return R.ok(exportService.executeExport(configId));
    }

    /**
     * 获取导出历史详情
     *
     * @param id 历史ID
     * @return 导出历史详情
     */
    @GetMapping("/history/{id}")
    @Operation(summary = "获取导出历史详情", description = "根据ID获取导出历史详情")
    public R<ExportHistoryVO> getExportHistory(@PathVariable String id) {
        return R.ok(exportService.getExportHistory(id));
    }

    /**
     * 查询导出历史列表
     *
     * @param configId  配置ID
     * @param status    状态
     * @param createdBy 创建人
     * @param pageable  分页参数
     * @return 分页结果
     */
    @GetMapping("/history")
    @Operation(summary = "查询导出历史列表", description = "根据条件查询导出历史列表")
    public R<Page<ExportHistoryVO>> getExportHistories(
            @RequestParam(required = false) String configId,
            @RequestParam(required = false) ExportStatus status,
            @RequestParam(required = false) String createdBy,
            @PageableDefault(size = 10) Pageable pageable) {
        return R.ok(exportService.findExportHistories(configId, status, createdBy, pageable));
    }

    /**
     * 取消导出任务
     *
     * @param id 历史ID
     * @return 更新后的历史记录
     */
    @PostMapping("/history/{id}/cancel")
    @Operation(summary = "取消导出任务", description = "取消正在进行的导出任务")
    public R<ExportHistoryVO> cancelExport(@PathVariable String id) {
        return R.ok(exportService.cancelExport(id));
    }

    /**
     * 下载导出文件
     *
     * @param id 历史ID
     * @return 文件资源
     */
    @GetMapping("/download/{id}")
    @Operation(summary = "下载导出文件", description = "根据历史ID下载导出文件")
    public ResponseEntity<Resource> downloadExportFile(@PathVariable String id) {
        ExportHistoryVO history = exportService.getExportHistory(id);
        byte[] fileData = exportService.downloadExportFile(id);
        
        ByteArrayResource resource = new ByteArrayResource(fileData);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + URLEncoder.encode(history.getFileName(), StandardCharsets.UTF_8) + "\"")
                .contentLength(fileData.length)
                .body(resource);
    }

    /**
     * 删除导出历史记录
     *
     * @param id 历史ID
     * @return 操作结果
     */
    @DeleteMapping("/history/{id}")
    @Operation(summary = "删除导出历史记录", description = "根据ID删除导出历史记录")
    public R<Void> deleteExportHistory(@PathVariable String id) {
        exportService.deleteExportHistory(id);
        return R.ok();
    }

    /**
     * 批量删除导出历史记录
     *
     * @param ids 历史ID列表
     * @return 操作结果
     */
    @DeleteMapping("/history/batch")
    @Operation(summary = "批量删除导出历史记录", description = "批量删除导出历史记录")
    public R<Void> batchDeleteExportHistories(@RequestBody List<String> ids) {
        exportService.batchDeleteExportHistories(ids);
        return R.ok();
    }
}