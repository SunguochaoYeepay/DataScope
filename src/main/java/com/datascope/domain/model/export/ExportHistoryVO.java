package com.datascope.domain.model.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 导出历史值对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportHistoryVO {
    
    /**
     * 主键ID
     */
    private String id;
    
    /**
     * 导出配置ID
     */
    private String exportConfigId;
    
    /**
     * 导出配置名称
     */
    private String configName;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 导出行数
     */
    private Integer rowCount;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态
     */
    private ExportStatus status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 执行人
     */
    private String createdBy;
    
    /**
     * 从实体转换为VO
     */
    public static ExportHistoryVO fromEntity(ExportHistory entity) {
        if (entity == null) {
            return null;
        }
        
        return ExportHistoryVO.builder()
                .id(entity.getId())
                .exportConfigId(entity.getExportConfigId())
                .fileName(entity.getFileName())
                .fileSize(entity.getFileSize())
                .rowCount(entity.getRowCount())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}