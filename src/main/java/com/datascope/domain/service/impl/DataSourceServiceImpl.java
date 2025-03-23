package com.datascope.domain.service.impl;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import com.datascope.domain.repository.DataSourceRepository;
import com.datascope.domain.service.DataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository dataSourceRepository;

    @Override
    @Transactional
    public DataSource createDataSource(DataSourceVO dataSourceVO) {
        // 检查名称是否已存在
        if (dataSourceRepository.existsByName(dataSourceVO.getName())) {
            throw new IllegalArgumentException("数据源名称已存在: " + dataSourceVO.getName());
        }

        // 转换并保存实体
        DataSource dataSource = dataSourceVO.toEntity();
        dataSource.setStatus(DataSourceStatus.INACTIVE);
        return dataSourceRepository.save(dataSource);
    }

    @Override
    @Transactional
    public DataSource updateDataSource(String id, DataSourceVO dataSourceVO) {
        // 获取现有数据源
        DataSource existingDataSource = dataSourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("数据源不存在: " + id));

        // 检查名称是否与其他数据源重复
        if (!existingDataSource.getName().equals(dataSourceVO.getName()) &&
            dataSourceRepository.existsByName(dataSourceVO.getName())) {
            throw new IllegalArgumentException("数据源名称已存在: " + dataSourceVO.getName());
        }

        // 更新属性
        existingDataSource.setName(dataSourceVO.getName());
        existingDataSource.setType(dataSourceVO.getType());
        existingDataSource.setHost(dataSourceVO.getHost());
        existingDataSource.setPort(dataSourceVO.getPort());
        existingDataSource.setUsername(dataSourceVO.getUsername());
        existingDataSource.setPassword(dataSourceVO.getPassword());
        existingDataSource.setDatabase(dataSourceVO.getDatabase());
        existingDataSource.setProperties(dataSourceVO.getProperties());
        existingDataSource.setDescription(dataSourceVO.getDescription());

        return dataSourceRepository.save(existingDataSource);
    }

    @Override
    @Transactional
    public void deleteDataSource(String id) {
        if (!dataSourceRepository.existsById(id)) {
            throw new EntityNotFoundException("数据源不存在: " + id);
        }
        dataSourceRepository.deleteById(id);
    }

    @Override
    public Optional<DataSource> findDataSourceById(String id) {
        return dataSourceRepository.findById(id);
    }

    @Override
    public Optional<DataSource> findDataSourceByName(String name) {
        return dataSourceRepository.findByName(name);
    }

    @Override
    public Page<DataSource> findDataSources(String name, DataSourceType type, DataSourceStatus status, Pageable pageable) {
        return dataSourceRepository.findByConditions(name, type, status, pageable);
    }

    @Override
    public List<DataSource> findDataSourcesByType(DataSourceType type) {
        return dataSourceRepository.findByType(type);
    }

    @Override
    public List<DataSource> findDataSourcesByStatus(DataSourceStatus status) {
        return dataSourceRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public boolean testDataSourceConnection(String id) {
        DataSource dataSource = dataSourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("数据源不存在: " + id));

        try {
            // 加载驱动
            Class.forName(dataSource.getDriverClassName());

            // 尝试建立连接
            try (Connection conn = DriverManager.getConnection(
                    dataSource.generateJdbcUrl(),
                    dataSource.getUsername(),
                    dataSource.getPassword())) {
                
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

    @Override
    @Transactional
    public DataSource updateDataSourceStatus(String id, DataSourceStatus status) {
        DataSource dataSource = dataSourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("数据源不存在: " + id));
        
        dataSource.setStatus(status);
        return dataSourceRepository.save(dataSource);
    }

    @Override
    public DataSourceStatistics getDataSourceStatistics() {
        return new DataSourceStatistics() {
            @Override
            public long getTotalCount() {
                return dataSourceRepository.count();
            }

            @Override
            public long getActiveCount() {
                return dataSourceRepository.countByStatus(DataSourceStatus.ACTIVE);
            }

            @Override
            public long getInactiveCount() {
                return dataSourceRepository.countByStatus(DataSourceStatus.INACTIVE);
            }

            @Override
            public long getErrorCount() {
                return dataSourceRepository.countByStatus(DataSourceStatus.ERROR);
            }

            @Override
            public long getTestingCount() {
                return dataSourceRepository.countByStatus(DataSourceStatus.TESTING);
            }

            @Override
            public long getMaintainingCount() {
                return dataSourceRepository.countByStatus(DataSourceStatus.MAINTAINING);
            }

            @Override
            public long getRelationalCount() {
                return Stream.of(DataSourceType.MYSQL, DataSourceType.POSTGRESQL,
                               DataSourceType.ORACLE, DataSourceType.SQLSERVER,
                               DataSourceType.DB2)
                        .mapToLong(dataSourceRepository::countByType)
                        .sum();
            }

            @Override
            public long getBigDataCount() {
                return dataSourceRepository.countByType(DataSourceType.HIVE);
            }

            @Override
            public long getAnalyticalCount() {
                return Stream.of(DataSourceType.CLICKHOUSE, DataSourceType.DORIS)
                        .mapToLong(dataSourceRepository::countByType)
                        .sum();
            }
        };
    }

    @Override
    public List<DataSource> getRecentlyModifiedDataSources(int limit) {
        return dataSourceRepository.findRecentlyModified(Pageable.ofSize(limit));
    }
}