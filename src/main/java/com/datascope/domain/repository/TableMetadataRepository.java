package com.datascope.domain.repository;

import com.datascope.domain.entity.TableMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableMetadataRepository extends JpaRepository<TableMetadataEntity, Long> {
    
    List<TableMetadataEntity> findByDataSourceId(Long dataSourceId);
    
    Optional<TableMetadataEntity> findByDataSourceIdAndSchemaAndName(
        Long dataSourceId, String schema, String name);
    
    @Query("SELECT DISTINCT t.schema FROM TableMetadataEntity t WHERE t.dataSourceId = ?1")
    List<String> findSchemasByDataSourceId(Long dataSourceId);
    
    List<TableMetadataEntity> findByDataSourceIdAndSchema(Long dataSourceId, String schema);
    
    void deleteByDataSourceId(Long dataSourceId);
}