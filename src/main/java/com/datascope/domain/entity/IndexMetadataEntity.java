package com.datascope.domain.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "index_metadata")
public class IndexMetadataEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "table_metadata_id", nullable = false)
  private Long tableMetadataId;

  @Column(name = "index_name", nullable = false)
  private String name;

  @Column(name = "is_unique")
  private boolean unique;

  @Column(name = "cardinality")
  private Long cardinality;

  @Column(name = "index_size")
  private Long indexSize;

  @Column(name = "column_names", nullable = false)
  private String columnNames;

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
