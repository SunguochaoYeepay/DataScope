package com.datascope.domain.entity;

import com.datascope.domain.model.metadata.ColumnType;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "column_metadata")
public class ColumnMetadataEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "table_metadata_id", nullable = false)
  private Long tableMetadataId;

  @Column(name = "column_name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "column_type", nullable = false)
  private ColumnType type;

  @Column(name = "column_length")
  private Integer length;

  @Column(name = "column_precision")
  private Integer precision;

  @Column(name = "is_nullable")
  private boolean nullable;

  @Column(name = "is_primary_key")
  private boolean primaryKey;

  @Column(name = "ordinal_position")
  private Integer ordinalPosition;

  @Column(name = "default_value")
  private String defaultValue;

  @Column(name = "column_comment")
  private String comment;

  @Column(name = "is_auto_increment")
  private boolean autoIncrement;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
