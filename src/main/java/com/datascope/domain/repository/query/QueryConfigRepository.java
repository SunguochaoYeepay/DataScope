package com.datascope.domain.repository.query;

import com.datascope.domain.model.query.QueryConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 查询配置仓库接口
 */
@Repository
public interface QueryConfigRepository extends JpaRepository<QueryConfig, String> {

    /**
     * 根据名称查找查询配置
     */
    Optional<QueryConfig> findByName(String name);

    /**
     * 检查名称是否已存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据数据源ID查找查询配置
     */
    List<QueryConfig> findByDataSourceId(String dataSourceId);
    
    /**
     * 分页查询查询配置
     */
    @Query("SELECT qc FROM QueryConfig qc WHERE " +
            "(:name IS NULL OR qc.name LIKE %:name%) AND " +
            "(:dataSourceId IS NULL OR qc.dataSourceId = :dataSourceId)")
    Page<QueryConfig> findByConditions(
            @Param("name") String name,
            @Param("dataSourceId") String dataSourceId,
            Pageable pageable);
            
    /**
     * 查找用户创建的查询配置
     */
    List<QueryConfig> findByCreatedBy(String createdBy);
} 