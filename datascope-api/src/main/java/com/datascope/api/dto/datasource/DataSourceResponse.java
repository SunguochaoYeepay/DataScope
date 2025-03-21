package com.datascope.api.dto.datasource;

import com.datascope.domain.model.datasource.DataSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据源响应DTO
 */
@Schema(description = "数据源响应信息")
@Data
public class DataSourceResponse {
    
    /**
     * 数据源ID
     */
    @Schema(description = "数据源ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    /**
     * 数据源名称
     */
    @Schema(description = "数据源名称", example = "test_mysql")
    private String name;

    /**
     * 数据源类型
     */
    @Schema(description = "数据源类型：MYSQL/DB2", example = "MYSQL")
    private DataSourceType type;

    /**
     * 主机地址
     */
    @Schema(description = "数据库主机地址", example = "localhost")
    private String host;

    /**
     * 端口号
     */
    @Schema(description = "数据库端口号", example = "3306")
    private Integer port;

    /**
     * 数据库名称
     */
    @Schema(description = "数据库名称", example = "test_db")
    private String databaseName;

    /**
     * 用户名
     */
    @Schema(description = "数据库用户名", example = "root")
    private String username;

    /**
     * 连接参数（JSON格式）
     */
    @Schema(description = "连接参数，JSON格式", example = "{\"useSSL\":false}")
    private String parameters;

    /**
     * 描述
     */
    @Schema(description = "数据源描述", example = "测试数据源")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-03-21T10:00:00")
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2024-03-21T10:00:00")
    private LocalDateTime modifiedAt;

    /**
     * 创建人
     */
    @Schema(description = "创建人 ID", example = "admin")
    private String createdBy;

    /**
     * 修改人
     */
    @Schema(description = "修改人 ID", example = "admin")
    private String modifiedBy;
}