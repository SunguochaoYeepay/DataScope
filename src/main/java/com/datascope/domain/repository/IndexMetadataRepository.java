package com.datascope.domain.repository;

import com.datascope.domain.entity.IndexMetadataEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexMetadataRepository extends JpaRepository<IndexMetadataEntity, Long> {

  List<IndexMetadataEntity> findByTableMetadataId(Long tableMetadataId);

  void deleteByTableMetadataId(Long tableMetadataId);
}
