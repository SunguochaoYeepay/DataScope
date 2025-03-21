package com.datascope.api.dto.datasource;

import com.datascope.domain.model.datasource.DataSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建数据源请求DTO
 */
@Data
@Schema(description = "创建数据源请求")
public class CreateDataSourceRequest {
    
    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称不能为空")
    @Size(max = 100, message = "数据源名称长度不能超过100个字符")
    private String name;

    /**
     * 数据源类型
     */
    @NotNull(message = "数据源类型不能为空")
    private DataSourceType type;

    /**
     * 主机地址
     */
    @NotBlank(message = "主机地址不能为空")
    @Size(max = 255, message = "主机地址长度不能超过255个字符")
    private String host;

    /**
     * 端口号
     */
    @NotNull(message = "端口号不能为空")
    private Integer port;

    /**
     * 数据库名称
     */
    @NotBlank(message = "数据库名称不能为空")
    @Size(max = 100, message = "数据库名称长度不能超过100个字符")
    private String databaseName;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 100, message = "用户名长度不能超过100个字符")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = 255, message = "密码长度不能超过255个字符")
    private String password;

    /**
     * 连接参数（JSON格式）
     */
    private String parameters;

    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;
}