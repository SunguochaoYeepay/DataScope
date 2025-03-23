package com.datascope.domain.repository;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.DataSourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, String>, JpaSpecificationExecutor<DataSource> {

    /**
     * 根据名称查找数据源
     */
    Optional<DataSource> findByName(String name);

    /**
     * 根据名称检查数据源是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据类型查找数据源列表
     */
    List<DataSource> findByType(DataSourceType type);

    /**
     * 根据状态查找数据源列表
     */
    List<DataSource> findByStatus(DataSourceStatus status);

    /**
     * 分页查询数据源
     */
    @Query("SELECT ds FROM DataSource ds WHERE " +
           "(:name IS NULL OR ds.name LIKE %:name%) AND " +
           "(:type IS NULL OR ds.type = :type) AND " +
           "(:status IS NULL OR ds.status = :status)")
    Page<DataSource> findByConditions(
            @Param("name") String name,
            @Param("type") DataSourceType type,
            @Param("status") DataSourceStatus status,
            Pageable pageable);

    /**
     * 根据类型统计数据源数量
     */
    long countByType(DataSourceType type);

    /**
     * 根据状态统计数据源数量
     */
    long countByStatus(DataSourceStatus status);

    /**
     * 查找指定时间段内创建的数据源
     */
    @Query("SELECT ds FROM DataSource ds WHERE ds.createdTime >= :startTime AND ds.createdTime <= :endTime")
    List<DataSource> findByCreatedTimeBetween(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    /**
     * 查找最近修改的数据源
     */
    @Query("SELECT ds FROM DataSource ds ORDER BY ds.lastModifiedTime DESC")
    List<DataSource> findRecentlyModified(Pageable pageable);
}