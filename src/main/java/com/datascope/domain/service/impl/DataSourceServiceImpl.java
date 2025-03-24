package com.datascope.domain.service.impl;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import com.datascope.domain.repository.DataSourceRepository;
import com.datascope.domain.service.datasource.DataSourceService;
import com.datascope.infrastructure.config.DataSourceConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

  private final DataSourceRepository dataSourceRepository;
  private final DataSourceConfig dataSourceConfig;

  // 缓存数据源连接池
  private final ConcurrentMap<String, HikariDataSource> dataSourceCache = new ConcurrentHashMap<>();

  @Override
  @Transactional
  public DataSourceVO create(DataSourceVO dataSourceVO) {
    // 检查数据源名称是否已存在
    if (dataSourceRepository.existsByName(dataSourceVO.getName())) {
      throw new IllegalArgumentException("数据源名称已存在");
    }

    // 创建数据源实体
    DataSource dataSource = new DataSource();
    dataSource.setName(dataSourceVO.getName());
    dataSource.setType(dataSourceVO.getType());
    dataSource.setHost(dataSourceVO.getHost());
    dataSource.setPort(dataSourceVO.getPort());
    dataSource.setUsername(dataSourceVO.getUsername());
    dataSource.setPassword(dataSourceVO.getPassword());
    dataSource.setDatabaseName(dataSourceVO.getDatabaseName());
    dataSource.setDescription(dataSourceVO.getDescription());
    dataSource.setStatus(DataSourceStatus.INACTIVE);
    
    // 如果 JPA 审计功能未生效，手动设置创建和更新信息
    LocalDateTime now = LocalDateTime.now();
    if (dataSource.getCreatedAt() == null) {
      dataSource.setCreatedAt(now);
      dataSource.setCreatedBy("admin");
    }
    if (dataSource.getUpdatedAt() == null) {
      dataSource.setUpdatedAt(now);
      dataSource.setUpdatedBy("admin");
    }

    // 测试连接
    testConnection(dataSource);

    // 保存数据源并返回VO
    dataSource = dataSourceRepository.save(dataSource);
    return DataSourceVO.fromEntity(dataSource);
  }

  @Override
  @Transactional
  public DataSourceVO update(String id, DataSourceVO dataSourceVO) {
    // 获取现有数据源
    DataSource existingDataSource =
        dataSourceRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("数据源不存在: " + id));

    // 检查名称是否与其他数据源重复
    if (!existingDataSource.getName().equals(dataSourceVO.getName())
        && dataSourceRepository.existsByName(dataSourceVO.getName())) {
      throw new IllegalArgumentException("数据源名称已存在: " + dataSourceVO.getName());
    }

    // 更新属性
    existingDataSource.setName(dataSourceVO.getName());
    existingDataSource.setType(dataSourceVO.getType());
    existingDataSource.setHost(dataSourceVO.getHost());
    existingDataSource.setPort(dataSourceVO.getPort());
    existingDataSource.setUsername(dataSourceVO.getUsername());
    existingDataSource.setPassword(dataSourceVO.getPassword());
    existingDataSource.setDatabaseName(dataSourceVO.getDatabaseName());
    existingDataSource.setProperties(dataSourceVO.getProperties());
    existingDataSource.setDescription(dataSourceVO.getDescription());
    
    // 如果 JPA 审计功能未生效，手动设置更新信息
    if (existingDataSource.getUpdatedAt() == null) {
      existingDataSource.setUpdatedAt(LocalDateTime.now());
      existingDataSource.setUpdatedBy("admin");
    }

    existingDataSource = dataSourceRepository.save(existingDataSource);
    return DataSourceVO.fromEntity(existingDataSource);
  }

  @Override
  @Transactional
  public void delete(String id) {
    // 先关闭连接池
    closeDataSource(id);
    // 再删除数据源
    dataSourceRepository.deleteById(id);
  }

  @Override
  public DataSourceVO get(String id) {
    return dataSourceRepository
        .findById(id)
        .map(DataSourceVO::fromEntity)
        .orElseThrow(() -> new EntityNotFoundException("数据源不存在: " + id));
  }

  @Override
  public List<DataSourceVO> list() {
    return dataSourceRepository.findAll().stream()
        .map(DataSourceVO::fromEntity)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public Boolean testDataSourceConnection(String id) {
    DataSource dataSource =
        dataSourceRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("数据源不存在: " + id));

    try {
      // 获取或创建连接池
      HikariDataSource hikariDataSource = getOrCreateDataSource(dataSource);

      // 尝试获取连接
      try (Connection conn = hikariDataSource.getConnection()) {
        // 更新状态为活跃
        dataSource.setStatus(DataSourceStatus.ACTIVE);
        dataSourceRepository.save(dataSource);
        return true;
      }
    } catch (Exception e) {
      log.error("测试数据源连接失败: {}", id, e);
      // 更新状态为错误
      dataSource.setStatus(DataSourceStatus.ERROR);
      dataSourceRepository.save(dataSource);
      return false;
    }
  }

  /** 获取或创建数据源连接池 */
  private HikariDataSource getOrCreateDataSource(DataSource dataSource) {
    return dataSourceCache.computeIfAbsent(
        dataSource.getId(),
        key -> {
          try {
            HikariConfig config;
            if (dataSource.getType() == DataSourceType.POSTGRESQL) {
              config =
                  dataSourceConfig.createPostgreSQLConfig(
                      dataSource.getHost(),
                      dataSource.getPort(),
                      dataSource.getDatabaseName(),
                      dataSource.getUsername(),
                      dataSource.getPassword());
            } else if (dataSource.getType() == DataSourceType.MYSQL) {
              config =
                  dataSourceConfig.createMySQLConfig(
                      dataSource.getHost(),
                      dataSource.getPort(),
                      dataSource.getDatabaseName(),
                      dataSource.getUsername(),
                      dataSource.getPassword());
            } else {
              // 其他数据库类型的配置...
              throw new UnsupportedOperationException(
                  "Unsupported database type: " + dataSource.getType());
            }
            return dataSourceConfig.createDataSource(config);
          } catch (Exception e) {
            log.error("创建数据源连接池失败: {}", key, e);
            throw new RuntimeException("Failed to create data source pool", e);
          }
        });
  }

  /** 关闭数据源连接池 */
  private void closeDataSource(String dataSourceId) {
    HikariDataSource ds = dataSourceCache.remove(dataSourceId);
    if (ds != null && !ds.isClosed()) {
      ds.close();
    }
  }

  /** 获取数据源连接 */
  public Connection getConnection(DataSource dataSource) throws SQLException {
    try {
      // 获取或创建连接池
      HikariDataSource hikariDataSource = getOrCreateDataSource(dataSource);
      return hikariDataSource.getConnection();
    } catch (SQLException e) {
      log.error("获取数据源连接失败: {}", dataSource.getId(), e);
      throw e;
    }
  }

  /** 测试数据源连接 */
  private void testConnection(DataSource dataSource) {
    try {
      HikariConfig config = switch (dataSource.getType()) {
        case MYSQL -> dataSourceConfig.createMySQLConfig(
            dataSource.getHost(),
            dataSource.getPort(),
            dataSource.getDatabaseName(),
            dataSource.getUsername(),
            dataSource.getPassword()
        );
        case POSTGRESQL -> dataSourceConfig.createPostgreSQLConfig(
            dataSource.getHost(),
            dataSource.getPort(),
            dataSource.getDatabaseName(),
            dataSource.getUsername(),
            dataSource.getPassword()
        );
        default -> throw new UnsupportedOperationException("暂不支持的数据源类型: " + dataSource.getType());
      };

      try (HikariDataSource ds = new HikariDataSource(config);
          Connection conn = ds.getConnection()) {
        log.info("数据源连接测试成功: {}", dataSource.getName());
      }
    } catch (SQLException e) {
      log.error("数据源连接测试失败: {}, 错误: {}", dataSource.getName(), e.getMessage());
      throw new IllegalStateException("数据源连接测试失败: " + e.getMessage());
    }
  }
}
