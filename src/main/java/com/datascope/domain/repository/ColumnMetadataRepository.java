package com.datascope.domain.repository;

import com.datascope.domain.entity.ColumnMetadataEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadataEntity, Long> {

  List<ColumnMetadataEntity> findByTableMetadataId(Long tableMetadataId);

  void deleteByTableMetadataId(Long tableMetadataId);
}
