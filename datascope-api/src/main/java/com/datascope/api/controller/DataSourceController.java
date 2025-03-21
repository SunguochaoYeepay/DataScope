package com.datascope.api.controller;

import com.datascope.api.dto.datasource.DataSourceRequest;
import com.datascope.api.dto.datasource.DataSourceResponse;
import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.service.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据源管理接口
 */
@Tag(name = "数据源管理", description = "提供数据源的增删改查和连接测试功能")
@RestController
@RequestMapping("/api/v1/data-sources")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Operation(
        summary = "创建数据源",
        description = "创建一个新的数据源配置，并测试连接是否成功",
        tags = {"数据源管理"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "409", description = "数据源名称已存在"),
        @ApiResponse(responseCode = "500", description = "数据源连接失败")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataSourceResponse createDataSource(
        @Valid @RequestBody DataSourceRequest request) {
        DataSource dataSource = convertToEntity(request);
        return convertToDTO(dataSourceService.createDataSource(dataSource));
    }

    @Operation(
        summary = "更新数据源",
        description = "更新指定ID的数据源配置，并测试新的连接是否成功",
        tags = {"数据源管理"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "数据源不存在"),
        @ApiResponse(responseCode = "409", description = "数据源名称已存在"),
        @ApiResponse(responseCode = "500", description = "数据源连接失败")
    })
    @PutMapping("/{id}")
    public DataSourceResponse updateDataSource(
        @Parameter(description = "数据源ID") @PathVariable String id,
        @Valid @RequestBody DataSourceRequest request) {
        DataSource dataSource = convertToEntity(request);
        return convertToDTO(dataSourceService.updateDataSource(id, dataSource));
    }

    @Operation(
        summary = "获取数据源详情",
        description = "获取指定ID的数据源配置信息",
        tags = {"数据源管理"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "数据源不存在")
    })
    @GetMapping("/{id}")
    public DataSourceResponse getDataSource(
        @Parameter(description = "数据源ID") @PathVariable String id) {
        return convertToDTO(dataSourceService.getDataSource(id));
    }

    @Operation(
        summary = "获取所有数据源",
        description = "获取所有数据源的配置信息列表",
        tags = {"数据源管理"})
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping
    public List<DataSourceResponse> getAllDataSources() {
        return dataSourceService.getAllDataSources().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Operation(
        summary = "删除数据源",
        description = "删除指定ID的数据源配置",
        tags = {"数据源管理"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "数据源不存在")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDataSource(
        @Parameter(description = "数据源ID") @PathVariable String id) {
        dataSourceService.deleteDataSource(id);
    }

    @Operation(
        summary = "测试数据源连接",
        description = "测试数据源配置的连接是否可用，不会保存配置",
        tags = {"数据源管理"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "测试成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "连接失败")
    })
    @PostMapping("/test-connection")
    public boolean testConnection(
        @Valid @RequestBody DataSourceRequest request) {
        DataSource dataSource = convertToEntity(request);
        return dataSourceService.testConnection(dataSource);
    }

    /**
     * 将请求DTO转换为实体对象
     */
    private DataSource convertToEntity(DataSourceRequest request) {
        DataSource dataSource = new DataSource();
        BeanUtils.copyProperties(request, dataSource);
        return dataSource;
    }

    /**
     * 将实体对象转换为响应DTO
     */
    private DataSourceResponse convertToDTO(DataSource dataSource) {
        DataSourceResponse response = new DataSourceResponse();
        BeanUtils.copyProperties(dataSource, response);
        return response;
    }
}