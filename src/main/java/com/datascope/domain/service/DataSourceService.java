package com.datascope.domain.service;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DataSourceService {

    /**
     * 创建数据源
     */
    DataSource createDataSource(DataSourceVO dataSourceVO);

    /**
     * 更新数据源
     */
    DataSource updateDataSource(String id, DataSourceVO dataSourceVO);

    /**
     * 删除数据源
     */
    void deleteDataSource(String id);

    /**
     * 根据ID查找数据源
     */
    Optional<DataSource> findDataSourceById(String id);

    /**
     * 根据名称查找数据源
     */
    Optional<DataSource> findDataSourceByName(String name);

    /**
     * 分页查询数据源
     */
    Page<DataSource> findDataSources(String name, DataSourceType type, DataSourceStatus status, Pageable pageable);

    /**
     * 根据类型查找数据源列表
     */
    List<DataSource> findDataSourcesByType(DataSourceType type);

    /**
     * 根据状态查找数据源列表
     */
    List<DataSource> findDataSourcesByStatus(DataSourceStatus status);

    /**
     * 测试数据源连接
     */
    boolean testDataSourceConnection(String id);

    /**
     * 更新数据源状态
     */
    DataSource updateDataSourceStatus(String id, DataSourceStatus status);

    /**
     * 获取数据源统计信息
     */
    DataSourceStatistics getDataSourceStatistics();

    /**
     * 获取最近修改的数据源
     */
    List<DataSource> getRecentlyModifiedDataSources(int limit);

    /**
     * 数据源统计信息
     */
    interface DataSourceStatistics {
        long getTotalCount();
        long getActiveCount();
        long getInactiveCount();
        long getErrorCount();
        long getTestingCount();
        long getMaintainingCount();
        long getRelationalCount();
        long getBigDataCount();
        long getAnalyticalCount();
    }
}