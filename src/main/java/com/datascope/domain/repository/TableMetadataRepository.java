package com.datascope.domain.repository;

import com.datascope.domain.entity.TableMetadataEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TableMetadataRepository extends JpaRepository<TableMetadataEntity, Long> {

  List<TableMetadataEntity> findByDataSourceId(String dataSourceId);

  Optional<TableMetadataEntity> findByDataSourceIdAndSchemaAndName(
      String dataSourceId, String schema, String name);

  @Query("SELECT DISTINCT t.schema FROM TableMetadataEntity t WHERE t.dataSourceId = ?1")
  List<String> findSchemasByDataSourceId(String dataSourceId);

  List<TableMetadataEntity> findByDataSourceIdAndSchema(String dataSourceId, String schema);

  void deleteByDataSourceId(String dataSourceId);
}
