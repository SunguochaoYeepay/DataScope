package com.datascope.domain.repository;

import com.datascope.domain.model.export.ExportConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 导出配置仓库
 */
@Repository
public interface ExportConfigRepository extends JpaRepository<ExportConfig, String> {

    /**
     * 根据名称查找导出配置
     */
    Optional<ExportConfig> findByName(String name);

    /**
     * 检查名称是否已存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据数据源ID查找导出配置
     */
    List<ExportConfig> findByDataSourceId(String dataSourceId);
    
    /**
     * 根据数据源ID和表名查找导出配置
     */
    List<ExportConfig> findByDataSourceIdAndTableName(String dataSourceId, String tableName);
    
    /**
     * 根据查询配置ID查找导出配置
     */
    List<ExportConfig> findByQueryConfigId(String queryConfigId);
    
    /**
     * 分页查询导出配置
     */
    @Query("SELECT ec FROM ExportConfig ec WHERE " +
            "(:name IS NULL OR ec.name LIKE %:name%) AND " +
            "(:dataSourceId IS NULL OR ec.dataSourceId = :dataSourceId) AND " +
            "(:tableName IS NULL OR ec.tableName = :tableName)")
    Page<ExportConfig> findByConditions(
            @Param("name") String name,
            @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName,
            Pageable pageable);
            
    /**
     * 查找用户创建的导出配置
     */
    List<ExportConfig> findByCreatedBy(String createdBy);
    
    /**
     * 根据创建时间降序查找最近创建的导出配置
     */
    @Query("SELECT ec FROM ExportConfig ec ORDER BY ec.createdAt DESC")
    List<ExportConfig> findRecentCreated(Pageable pageable);
}