package com.datascope.api.dto.datasource;

import com.datascope.domain.model.datasource.DataSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 测试数据源连接请求DTO
 */
@Data
@Schema(description = "测试数据源连接请求")
public class TestConnectionRequest {
    
    /**
     * 数据源类型
     */
    @Schema(description = "数据源类型")
    @NotNull(message = "数据源类型不能为空")
    private DataSourceType type;

    /**
     * 主机地址
     */
    @Schema(description = "主机地址")
    @NotBlank(message = "主机地址不能为空")
    private String host;

    /**
     * 端口号
     */
    @Schema(description = "端口号")
    @NotNull(message = "端口号不能为空")
    private Integer port;

    /**
     * 数据库名称
     */
    @Schema(description = "数据库名称")
    @NotBlank(message = "数据库名称不能为空")
    private String databaseName;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 连接参数（JSON格式）
     */
    @Schema(description = "连接参数（JSON格式）")
    private String parameters;
}