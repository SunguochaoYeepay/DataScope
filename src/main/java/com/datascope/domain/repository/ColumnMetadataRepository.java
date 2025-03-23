package com.datascope.domain.repository;

import com.datascope.domain.entity.ColumnMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadataEntity, Long> {
    
    List<ColumnMetadataEntity> findByTableMetadataId(Long tableMetadataId);
    
    void deleteByTableMetadataId(Long tableMetadataId);
}