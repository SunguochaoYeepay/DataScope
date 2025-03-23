package com.datascope.api.controller;

import com.datascope.api.dto.datasource.DataSourceRequest;
import com.datascope.api.dto.datasource.DataSourceResponse;
import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.TestConnectionResult;
import com.datascope.domain.service.DataSourceService;
import com.datascope.common.model.PageRequest;
import com.datascope.common.model.PageResponse;
import com.datascope.common.model.Response;
import com.datascope.common.exception.BizException;
import com.datascope.common.exception.ErrorCode;
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
@RequestMapping("/api/v1/datasources")
public class DataSourceController {

    /**
     * 支持的排序字段
     */
    private static final List<String> ALLOWED_SORT_FIELDS = List.of("name", "type", "status", "createTime", "updateTime");
    
    /**
     * 支持的排序方向
     */
    private static final List<String> ALLOWED_ORDER_DIRECTIONS = List.of("asc", "desc");

    /**
     * 支持的数据源类型
     */
    private static final List<String> SUPPORTED_DATASOURCE_TYPES = List.of(
        "mysql", "postgresql", "oracle", "sqlserver", "clickhouse", "doris");

    private final DataSourceService dataSourceService;

    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Operation(
        summary = "创建数据源",
        description = """
            创建一个新的数据源配置，并测试连接是否成功。
            
            请求示例：
            POST /api/v1/datasources
            {
                "name": "测试数据源",
                "type": "mysql",
                "host": "localhost",
                "port": 3306,
                "database": "test",
                "username": "root",
                "password": "******"
            }
            
            成功响应示例：
            {
                "code": 201,
                "message": "success",
                "requestId": "550e8400-e29b-41d4-a716-446655440000",
                "data": {
                    "id": "1",
                    "name": "测试数据源",
                    "type": "mysql",
                    "status": "enabled",
                    "createTime": "2024-03-20T10:00:00Z",
                    "updateTime": "2024-03-20T10:00:00Z"
                }
            }
            """,
        tags = {"数据源管理"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "创建成功",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DataSourceResponse.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "409", description = "数据源名称已存在"),
        @ApiResponse(responseCode = "500", description = "数据源连接失败")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<DataSourceResponse> createDataSource(
        @Valid @RequestBody DataSourceRequest request) {
        // 验证数据源类型
        validateDataSourceType(request.getType());
        
        // 验证数据源名称唯一性
        if (dataSourceService.existsByName(request.getName())) {
            throw new BizException(ErrorCode.DATASOURCE_NAME_EXISTS, 
                String.format("数据源名称[%s]已存在", request.getName()));
        }
        
        DataSource dataSource = convertToEntity(request);
        return Response.success(201, convertToDTO(dataSourceService.createDataSource(dataSource)));
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
    public Response<DataSourceResponse> updateDataSource(
        @Parameter(description = "数据源ID") @PathVariable String id,
        @Valid @RequestBody DataSourceRequest request) {
        // 验证数据源类型
        validateDataSourceType(request.getType());
        
        // 验证数据源名称唯一性（排除自身）
        if (dataSourceService.existsByNameAndIdNot(request.getName(), id)) {
            throw new BizException(ErrorCode.DATASOURCE_NAME_EXISTS, 
                String.format("数据源名称[%s]已存在", request.getName()));
        }
        
        DataSource dataSource = convertToEntity(request);
        return Response.success(convertToDTO(dataSourceService.updateDataSource(id, dataSource)));
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
    public Response<DataSourceResponse> getDataSource(
        @Parameter(description = "数据源ID") @PathVariable String id) {
        return Response.success(convertToDTO(dataSourceService.getDataSource(id)));
    }

    @Operation(
        summary = "获取所有数据源",
        description = """
            分页获取所有数据源的配置信息列表。
            
            处理流程：
            1. 验证分页参数(current, size)和排序参数(sort, order)
            2. 解析数据源状态枚举值
            3. 调用服务层执行分页查询
            4. 转换实体对象为DTO响应
            
            请求示例：
            GET /api/v1/datasources?current=1&size=10&keyword=test&type=mysql&status=enabled&sort=createTime&order=desc
            
            成功响应示例：
            {
                "code": 200,
                "message": "success",
                "requestId": "550e8400-e29b-41d4-a716-446655440000",
                "data": {
                    "total": 100,
                    "current": 1,
                    "size": 10,
                    "records": [
                        {
                            "id": "1",
                            "name": "测试数据源",
                            "type": "mysql",
                            "status": "enabled",
                            "createTime": "2024-03-20T10:00:00Z",
                            "updateTime": "2024-03-20T10:00:00Z"
                        }
                    ]
                }
            }
            
            错误响应示例：
            {
                "code": 400,
                "message": "无效的状态值: invalid_status。有效值为: enabled, disabled, connection_failed",
                "requestId": "550e8400-e29b-41d4-a716-446655440000"
            }
            """,
        tags = {"数据源管理"})
    @ApiResponse(responseCode = "200", description = "获取成功",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = PageResponse.class)))
    @GetMapping
    public Response<PageResponse<DataSourceResponse>> getDataSourcesByPage(
            @Parameter(description = "关键词，用于搜索数据源名称和描述")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "数据源类型，例如：mysql, postgresql, oracle等")
            @RequestParam(required = false) String type,
            
            @Parameter(description = "数据源状态：enabled-启用, disabled-禁用, connection_failed-连接失败")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "当前页码，从1开始")
            @RequestParam(defaultValue = "1") int current,
            
            @Parameter(description = "每页显示记录数，默认10条")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段，支持：name, type, status, createTime, updateTime")
            @RequestParam(required = false) String sort,
            
            @Parameter(description = "排序方向：asc-升序, desc-降序")
            @RequestParam(required = false) String order) {
        
        // 验证分页参数
        if (current < 1) {
            throw new BizException(ErrorCode.INVALID_PAGE_PARAMETER, "页码必须大于等于1");
        }
        if (size < 1 || size > 100) {
            throw new BizException(ErrorCode.INVALID_PAGE_PARAMETER, "每页大小必须在1-100之间");
        }
        
        // 验证排序参数
        if (sort != null && !ALLOWED_SORT_FIELDS.contains(sort)) {
            throw new BizException(ErrorCode.INVALID_SORT_PARAMETER, 
                String.format("无效的排序字段: %s。支持的排序字段: %s", 
                    sort, String.join(", ", ALLOWED_SORT_FIELDS)));
        }
        if (order != null && !ALLOWED_ORDER_DIRECTIONS.contains(order.toLowerCase())) {
            throw new BizException(ErrorCode.INVALID_SORT_PARAMETER,
                String.format("无效的排序方向: %s。支持的排序方向: %s",
                    order, String.join(", ", ALLOWED_ORDER_DIRECTIONS)));
        }
        
        // 构建分页请求
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(current);
        pageRequest.setSize(size);
        pageRequest.setSort(sort);
        pageRequest.setOrder(order);
        
        // 解析状态枚举
        DataSourceStatus statusEnum = null;
        if (status != null) {
            try {
                statusEnum = DataSourceStatus.fromString(status);
            } catch (IllegalArgumentException e) {
                throw new BizException(ErrorCode.INVALID_STATUS,
                    String.format("无效的状态值: %s。有效值为: enabled, disabled, connection_failed", status));
            }
        }
        
        // 执行分页查询
        PageResponse<DataSource> pageResult = dataSourceService.getDataSourcesByPage(
            keyword, type, statusEnum, pageRequest);
        
        // 转换响应数据
        List<DataSourceResponse> records = pageResult.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return Response.success(PageResponse.<DataSourceResponse>builder()
            .records(records)
            .total(pageResult.getTotal())
            .current(pageResult.getCurrent())
            .size(pageResult.getSize())
            .build());
    }

    @Operation(
        summary = "删除数据源",
        description = "删除指定ID的数据源配置",
        tags = {"数据源管理"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "数据源不存在"),
        @ApiResponse(responseCode = "409", description = "数据源正在使用中")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDataSource(
        @Parameter(description = "数据源ID") @PathVariable String id) {
        // 检查数据源是否存在
        DataSource dataSource = dataSourceService.getDataSource(id);
        
        // 检查数据源是否正在使用
        if (dataSourceService.isInUse(id)) {
            throw new BizException(ErrorCode.DATASOURCE_IN_USE, 
                String.format("数据源[%s]正在使用中，无法删除", dataSource.getName()));
        }
        
        dataSourceService.deleteDataSource(id);
    }

    @Operation(
        summary = "测试数据源连接",
        description = "测试数据源配置的连接是否可用，不会保存配置",
        tags = {"数据源管理"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "测试成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "连接失败")
    })
    @PostMapping("/test-connection")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<TestConnectionResult> testConnection(
        @Valid @RequestBody DataSourceRequest request) {
        DataSource dataSource = convertToEntity(request);
        return Response.success(201, dataSourceService.testConnection(dataSource));
    }

    /**
     * 将请求DTO转换为实体对象
     * 
     * @param request 数据源请求对象
     * @return 数据源实体对象
     */
    private @NonNull DataSource convertToEntity(@NonNull DataSourceRequest request) {
        DataSource dataSource = new DataSource();
        BeanUtils.copyProperties(request, dataSource);
        return dataSource;
    }

    /**
     * 将实体对象转换为响应DTO
     * 
     * @param dataSource 数据源实体对象
     * @return 数据源响应对象
     */
    private @NonNull DataSourceResponse convertToDTO(@NonNull DataSource dataSource) {
        DataSourceResponse response = new DataSourceResponse();
        BeanUtils.copyProperties(dataSource, response);
        return response;
    }

    /**
     * 验证数据源类型是否支持
     * 
     * @param type 数据源类型
     * @throws BizException 当数据源类型不支持时
     */
    private void validateDataSourceType(String type) {
        if (!SUPPORTED_DATASOURCE_TYPES.contains(type.toLowerCase())) {
            throw new BizException(ErrorCode.DATASOURCE_TYPE_NOT_SUPPORTED,
                String.format("不支持的数据源类型[%s]，支持的类型：%s", 
                    type, String.join(", ", SUPPORTED_DATASOURCE_TYPES)));
        }
    }
}