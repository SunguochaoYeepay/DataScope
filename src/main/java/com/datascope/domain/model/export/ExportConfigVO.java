package com.datascope.domain.model.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 导出配置值对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportConfigVO {
    
    /**
     * 主键ID
     */
    private String id;
    
    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称不能超过100个字符")
    private String name;
    
    /**
     * 查询配置ID
     */
    private String queryConfigId;
    
    /**
     * 数据源ID
     */
    @NotBlank(message = "数据源ID不能为空")
    private String dataSourceId;
    
    /**
     * 表名
     */
    @NotBlank(message = "表名不能为空")
    private String tableName;
    
    /**
     * 导出类型
     */
    @NotNull(message = "导出类型不能为空")
    private ExportType exportType;
    
    /**
     * 列配置
     */
    @NotNull(message = "列配置不能为空")
    private List<ColumnConfig> columnConfig;
    
    /**
     * 过滤配置
     */
    private FilterConfig filterConfig;
    
    /**
     * 从实体转换为VO
     */
    public static ExportConfigVO fromEntity(ExportConfig entity) {
        if (entity == null) {
            return null;
        }
        
        return ExportConfigVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .queryConfigId(entity.getQueryConfigId())
                .dataSourceId(entity.getDataSourceId())
                .tableName(entity.getTableName())
                .exportType(entity.getExportType())
                .columnConfig(entity.getColumnConfig())
                .filterConfig(entity.getFilterConfig())
                .build();
    }
    
    /**
     * 转换为实体
     */
    public ExportConfig toEntity() {
        return ExportConfig.builder()
                .id(this.id)
                .name(this.name)
                .queryConfigId(this.queryConfigId)
                .dataSourceId(this.dataSourceId)
                .tableName(this.tableName)
                .exportType(this.exportType)
                .columnConfig(this.columnConfig)
                .filterConfig(this.filterConfig)
                .build();
    }
}