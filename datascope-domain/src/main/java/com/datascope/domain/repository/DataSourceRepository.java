package com.datascope.domain.repository;

import com.datascope.domain.model.datasource.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * 数据源仓库接口
 */
public interface DataSourceRepository {
    
    /**
     * 保存数据源
     *
     * @param dataSource 数据源信息
     * @return 保存后的数据源
     */
    DataSource save(DataSource dataSource);

    /**
     * 根据ID查询数据源
     *
     * @param id 数据源ID
     * @return 数据源信息
     */
    Optional<DataSource> findById(String id);

    /**
     * 查询所有数据源
     *
     * @return 数据源列表
     */
    List<DataSource> findAll();

    /**
     * 根据名称查询数据源
     *
     * @param name 数据源名称
     * @return 数据源信息
     */
    Optional<DataSource> findByName(String name);

    /**
     * 删除数据源
     *
     * @param id 数据源ID
     */
    void deleteById(String id);

    /**
     * 检查数据源名称是否已存在
     *
     * @param name 数据源名称
     * @return 是否存在
     */
    boolean existsByName(String name);
}