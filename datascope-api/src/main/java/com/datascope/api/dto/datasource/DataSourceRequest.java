package com.datascope.api.dto.datasource;

import com.datascope.domain.model.datasource.DataSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 数据源请求DTO
 */
@Schema(description = "数据源请求参数")
@Data
public class DataSourceRequest {
    
    /**
     * 数据源名称
     */
    @Schema(description = "数据源名称", example = "test_mysql", required = true)
    @NotBlank(message = "数据源名称不能为空")
    private String name;

    /**
     * 数据源类型
     */
    @Schema(description = "数据源类型：MYSQL/DB2", example = "MYSQL", required = true)
    @NotNull(message = "数据源类型不能为空")
    private DataSourceType type;

    /**
     * 主机地址
     */
    @Schema(description = "数据库主机地址", example = "localhost", required = true)
    @NotBlank(message = "主机地址不能为空")
    private String host;

    /**
     * 端口号
     */
    @Schema(description = "数据库端口号", example = "3306", minimum = "1", maximum = "65535", required = true)
    @NotNull(message = "端口号不能为空")
    @Min(value = 1, message = "端口号必须大于0")
    @Max(value = 65535, message = "端口号必须小于65536")
    private Integer port;

    /**
     * 数据库名称
     */
    @Schema(description = "数据库名称", example = "test_db", required = true)
    @NotBlank(message = "数据库名称不能为空")
    private String databaseName;

    /**
     * 用户名
     */
    @Schema(description = "数据库用户名", example = "root", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "数据库密码", example = "123456", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 连接参数（JSON格式）
     */
    @Schema(description = "连接参数，JSON格式", example = "{\"useSSL\":false}", required = false)
    private String parameters;

    /**
     * 描述
     */
    @Schema(description = "数据源描述", example = "测试数据源", required = false)
    private String description;
}