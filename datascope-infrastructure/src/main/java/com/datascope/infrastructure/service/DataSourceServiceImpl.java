package com.datascope.infrastructure.service;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.repository.DataSourceRepository;
import com.datascope.domain.service.DataSourceService;
import com.datascope.infrastructure.datasource.DataSourceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * 数据源服务实现类
 */
@Service
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository dataSourceRepository;
    private final DataSourceManager dataSourceManager;

    @Override
    @Transactional
    public DataSource createDataSource(DataSource dataSource) {
        // 检查数据源名称是否已存在
        if (dataSourceRepository.existsByName(dataSource.getName())) {
            throw new IllegalArgumentException("数据源名称已存在: " + dataSource.getName());
        }

        // 测试数据源连接
        if (!testConnection(dataSource)) {
            throw new IllegalArgumentException("数据源连接测试失败");
        }

        return dataSourceRepository.save(dataSource);
    }

    @Override
    @Transactional
    public DataSource updateDataSource(String id, DataSource dataSource) {
        // 检查数据源是否存在
        DataSource existingDataSource = dataSourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("数据源不存在: " + id));

        // 如果更改了名称，检查新名称是否已存在
        if (!existingDataSource.getName().equals(dataSource.getName()) &&
                dataSourceRepository.existsByName(dataSource.getName())) {
            throw new IllegalArgumentException("数据源名称已存在: " + dataSource.getName());
        }

        // 测试新的连接配置
        if (!testConnection(dataSource)) {
            throw new IllegalArgumentException("数据源连接测试失败");
        }

        // 设置ID并保存
        dataSource.setId(id);
        DataSource updatedDataSource = dataSourceRepository.save(dataSource);

        // 移除旧的连接池
        dataSourceManager.removeDataSource(id);

        return updatedDataSource;
    }

    @Override
    @Transactional(readOnly = true)
    public DataSource getDataSource(String id) {
        return dataSourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("数据源不存在: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataSource> getAllDataSources() {
        return dataSourceRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteDataSource(String id) {
        // 检查数据源是否存在
        if (!dataSourceRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException("数据源不存在: " + id);
        }

        // 移除连接池
        dataSourceManager.removeDataSource(id);

        // 删除数据源记录
        dataSourceRepository.deleteById(id);
    }

    @Override
    public boolean testConnection(DataSource dataSource) {
        return dataSourceManager.testConnection(dataSource);
    }
}