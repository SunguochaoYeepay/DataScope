package com.datascope.domain.model.datasource;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 数据源实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSource {
    
    /**
     * 数据源ID
     */
    private String id;

    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称不能为空")
    private String name;

    /**
     * 数据源类型（如: MYSQL, DB2等）
     */
    @NotNull(message = "数据源类型不能为空")
    private DataSourceType type;

    /**
     * 主机地址
     */
    @NotBlank(message = "主机地址不能为空")
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
    private String databaseName;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 连接参数（JSON格式）
     */
    private String parameters;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;
}