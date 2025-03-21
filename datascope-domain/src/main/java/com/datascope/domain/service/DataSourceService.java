package com.datascope.domain.service;

import com.datascope.domain.model.datasource.DataSource;
import java.util.List;

/**
 * 数据源服务接口
 */
public interface DataSourceService {
    
    /**
     * 创建数据源
     *
     * @param dataSource 数据源信息
     * @return 创建后的数据源
     */
    DataSource createDataSource(DataSource dataSource);

    /**
     * 更新数据源
     *
     * @param id 数据源ID
     * @param dataSource 数据源信息
     * @return 更新后的数据源
     */
    DataSource updateDataSource(String id, DataSource dataSource);

    /**
     * 获取数据源详情
     *
     * @param id 数据源ID
     * @return 数据源信息
     */
    DataSource getDataSource(String id);

    /**
     * 获取所有数据源
     *
     * @return 数据源列表
     */
    List<DataSource> getAllDataSources();

    /**
     * 删除数据源
     *
     * @param id 数据源ID
     */
    void deleteDataSource(String id);

    /**
     * 测试数据源连接
     *
     * @param dataSource 数据源信息
     * @return 是否连接成功
     */
    boolean testConnection(DataSource dataSource);
}