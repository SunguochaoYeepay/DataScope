package com.datascope.domain.service.datasource;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/** DataSource service interface for managing data sources */
public interface DataSourceService {

  /**
   * Create a new data source
   *
   * @param dataSource Data source information
   * @return Created data source
   */
  DataSourceVO create(DataSourceVO dataSource);

  /**
   * Update an existing data source
   *
   * @param id Data source ID
   * @param dataSource Updated data source information
   * @return Updated data source
   */
  DataSourceVO update(String id, DataSourceVO dataSource);

  /**
   * Delete a data source
   *
   * @param id Data source ID
   */
  void delete(String id);

  /**
   * Get a data source by ID
   *
   * @param id Data source ID
   * @return Data source information
   */
  DataSourceVO get(String id);

  /**
   * List all data sources
   *
   * @return List of data sources
   */
  List<DataSourceVO> list();

  /**
   * 测试数据源连接
   *
   * @param id 数据源ID
   * @return 测试结果，true表示连接成功，false表示连接失败
   */
  Boolean testDataSourceConnection(String id);

  /**
   * 获取数据源连接
   *
   * @param dataSource 数据源实体
   * @return 数据库连接
   * @throws SQLException 如果获取连接失败
   */
  Connection getConnection(DataSource dataSource) throws SQLException;
}
