package com.datascope.facade.controller;

import com.datascope.domain.model.query.QueryConfig;
import com.datascope.domain.model.query.QueryHistory;
import com.datascope.domain.model.query.QueryRequest;
import com.datascope.domain.model.query.QueryResult;
import com.datascope.domain.service.query.QueryService;
import com.datascope.infrastructure.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询管理接口
 */
@Slf4j
@RestController
@RequestMapping("/v1/queries")
@RequiredArgsConstructor
@Validated
@Tag(name = "查询管理", description = "查询配置和执行相关接口")
public class QueryController {

    private final QueryService queryService;

    @PostMapping
    @Operation(summary = "创建查询配置", description = "创建新的SQL查询配置")
    public R<Map<String, Object>> createQueryConfig(@RequestBody @Valid QueryConfig queryConfig) {
        log.info("创建查询配置: {}", queryConfig.getName());
        
        QueryConfig created = queryService.createQueryConfig(queryConfig);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", created.getId());
        result.put("name", created.getName());
        result.put("version", 1);
        
        return R.ok(result);
    }
    
    @GetMapping
    @Operation(summary = "查询配置列表", description = "分页获取查询配置列表")
    public R<Page<QueryConfig>> getQueryConfigs(
            @Parameter(description = "配置名称") @RequestParam(required = false) String name,
            @Parameter(description = "数据源ID") @RequestParam(required = false) String dataSourceId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String order) {
        
        log.info("查询配置列表: name={}, dataSourceId={}, page={}, size={}", name, dataSourceId, page, size);
        
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sort));
        
        Page<QueryConfig> configs = queryService.findQueryConfigs(name, dataSourceId, pageable);
        
        return R.ok(configs);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "查询配置详情", description = "根据ID获取查询配置详情")
    public R<QueryConfig> getQueryConfig(
            @Parameter(description = "查询配置ID") @PathVariable @NotBlank String id) {
        
        log.info("查询配置详情: id={}", id);
        
        QueryConfig config = queryService.getQueryConfig(id);
        
        return R.ok(config);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新查询配置", description = "更新指定ID的查询配置")
    public R<QueryConfig> updateQueryConfig(
            @Parameter(description = "查询配置ID") @PathVariable @NotBlank String id,
            @RequestBody @Valid QueryConfig queryConfig) {
        
        log.info("更新查询配置: id={}, name={}", id, queryConfig.getName());
        
        QueryConfig updated = queryService.updateQueryConfig(id, queryConfig);
        
        return R.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除查询配置", description = "删除指定ID的查询配置")
    public R<Void> deleteQueryConfig(
            @Parameter(description = "查询配置ID") @PathVariable @NotBlank String id) {
        
        log.info("删除查询配置: id={}", id);
        
        queryService.deleteQueryConfig(id);
        
        return R.ok();
    }
    
    @PostMapping("/{id}/execute")
    @Operation(summary = "执行查询", description = "执行指定ID的查询配置")
    public R<QueryResult> executeQuery(
            @Parameter(description = "查询配置ID") @PathVariable @NotBlank String id,
            @RequestBody @Valid QueryRequest request) {
        
        log.info("执行查询: id={}", id);
        
        // 使用路径中的ID，强制覆盖请求中的ID，确保一致性
        request.setQueryConfigId(id);
        
        QueryResult result = queryService.executeQuery(request);
        
        return R.ok(result);
    }
    
    @GetMapping("/{id}/history")
    @Operation(summary = "查询历史记录", description = "获取指定查询配置的执行历史记录")
    public R<Page<QueryHistory>> getQueryHistory(
            @Parameter(description = "查询配置ID") @PathVariable @NotBlank String id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        
        log.info("查询历史记录: id={}, page={}, size={}", id, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size);
        
        Page<QueryHistory> history = queryService.getQueryHistory(id, pageable);
        
        return R.ok(history);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "最近执行的查询", description = "获取当前用户最近执行的查询历史")
    public R<List<QueryHistory>> getRecentQueries(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "5") @Min(1) Integer limit) {
        
        log.info("最近执行的查询: limit={}", limit);
        
        List<QueryHistory> recent = queryService.getRecentQueries(limit);
        
        return R.ok(recent);
    }
} 