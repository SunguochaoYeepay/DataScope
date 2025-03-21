package com.datascope.infrastructure.repository.impl;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.repository.DataSourceRepository;
import com.datascope.infrastructure.mapper.DataSourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 数据源仓库实现类
 */
@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryImpl implements DataSourceRepository {

    private final DataSourceMapper dataSourceMapper;

    @Override
    public DataSource save(DataSource dataSource) {
        if (dataSource.getId() == null) {
            // 新增数据源
            dataSource.setId(UUID.randomUUID().toString().replace("-", ""));
            dataSourceMapper.insert(dataSource);
        } else {
            // 更新数据源
            dataSourceMapper.update(dataSource);
        }
        return dataSource;
    }

    @Override
    public Optional<DataSource> findById(String id) {
        return dataSourceMapper.findById(id);
    }

    @Override
    public List<DataSource> findAll() {
        return dataSourceMapper.findAll();
    }

    @Override
    public Optional<DataSource> findByName(String name) {
        return dataSourceMapper.findByName(name);
    }

    @Override
    public void deleteById(String id) {
        dataSourceMapper.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return dataSourceMapper.existsByName(name);
    }
}