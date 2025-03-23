package com.datascope.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "table_metadata")
public class TableMetadataEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_source_id", nullable = false)
    private Long dataSourceId;

    @Column(name = "schema_name", nullable = false)
    private String schema;

    @Column(name = "table_name", nullable = false)
    private String name;

    @Column(name = "table_comment")
    private String comment;

    @Column(name = "row_count")
    private Long rowCount;

    @Column(name = "data_size")
    private Long dataSize;

    @Column(name = "index_size")
    private Long indexSize;

    @Column(name = "last_analyzed", nullable = false)
    private LocalDateTime lastAnalyzed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastAnalyzed = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}