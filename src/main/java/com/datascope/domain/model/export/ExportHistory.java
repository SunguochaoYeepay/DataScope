package com.datascope.domain.model.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.time.LocalDateTime;

/**
 * 导出历史实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "export_history")
public class ExportHistory {

    /**
     * 主键ID
     */
    @Id
    private String id;

    /**
     * 导出配置ID
     */
    @Column(name = "export_config_id", nullable = false, length = 36)
    private String exportConfigId;

    /**
     * 文件名
     */
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /**
     * 文件大小(字节)
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 导出行数
     */
    @Column(name = "row_count")
    private Integer rowCount;

    /**
     * 开始时间
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 状态
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ExportStatus status;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 执行人
     */
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
}