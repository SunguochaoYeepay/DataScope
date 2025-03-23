package com.datascope.domain.service;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import com.datascope.domain.repository.DataSourceRepository;
import com.datascope.domain.service.impl.DataSourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSourceServiceTest {

    @Mock
    private DataSourceRepository dataSourceRepository;

    @InjectMocks
    private DataSourceServiceImpl dataSourceService;

    private DataSourceVO dataSourceVO;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        dataSourceVO = new DataSourceVO();
        dataSourceVO.setName("test_db");
        dataSourceVO.setType(DataSourceType.MYSQL);
        dataSourceVO.setHost("localhost");
        dataSourceVO.setPort(3306);
        dataSourceVO.setUsername("root");
        dataSourceVO.setPassword("password");
        dataSourceVO.setDatabase("test");

        dataSource = dataSourceVO.toEntity();
        dataSource.setId("test-id");
        dataSource.setStatus(DataSourceStatus.INACTIVE);
    }

    @Test
    void createDataSource_Success() {
        // 设置模拟行为
        when(dataSourceRepository.existsByName(dataSourceVO.getName())).thenReturn(false);
        when(dataSourceRepository.save(any(DataSource.class))).thenReturn(dataSource);

        // 执行测试
        DataSource result = dataSourceService.createDataSource(dataSourceVO);

        // 验证结果
        assertNotNull(result);
        assertEquals(dataSourceVO.getName(), result.getName());
        assertEquals(DataSourceStatus.INACTIVE, result.getStatus());
        verify(dataSourceRepository).existsByName(dataSourceVO.getName());
        verify(dataSourceRepository).save(any(DataSource.class));
    }

    @Test
    void createDataSource_NameExists() {
        // 设置模拟行为
        when(dataSourceRepository.existsByName(dataSourceVO.getName())).thenReturn(true);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class,
                () -> dataSourceService.createDataSource(dataSourceVO));

        verify(dataSourceRepository).existsByName(dataSourceVO.getName());
        verify(dataSourceRepository, never()).save(any(DataSource.class));
    }

    @Test
    void updateDataSource_Success() {
        // 设置模拟行为
        when(dataSourceRepository.findById(dataSource.getId())).thenReturn(Optional.of(dataSource));
        when(dataSourceRepository.save(any(DataSource.class))).thenReturn(dataSource);

        // 执行测试
        DataSource result = dataSourceService.updateDataSource(dataSource.getId(), dataSourceVO);

        // 验证结果
        assertNotNull(result);
        assertEquals(dataSourceVO.getName(), result.getName());
        verify(dataSourceRepository).findById(dataSource.getId());
        verify(dataSourceRepository).save(any(DataSource.class));
    }

    @Test
    void updateDataSource_NotFound() {
        // 设置模拟行为
        when(dataSourceRepository.findById(dataSource.getId())).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(EntityNotFoundException.class,
                () -> dataSourceService.updateDataSource(dataSource.getId(), dataSourceVO));

        verify(dataSourceRepository).findById(dataSource.getId());
        verify(dataSourceRepository, never()).save(any(DataSource.class));
    }

    @Test
    void deleteDataSource_Success() {
        // 设置模拟行为
        when(dataSourceRepository.existsById(dataSource.getId())).thenReturn(true);

        // 执行测试
        dataSourceService.deleteDataSource(dataSource.getId());

        // 验证结果
        verify(dataSourceRepository).existsById(dataSource.getId());
        verify(dataSourceRepository).deleteById(dataSource.getId());
    }

    @Test
    void deleteDataSource_NotFound() {
        // 设置模拟行为
        when(dataSourceRepository.existsById(dataSource.getId())).thenReturn(false);

        // 执行测试并验证异常
        assertThrows(EntityNotFoundException.class,
                () -> dataSourceService.deleteDataSource(dataSource.getId()));

        verify(dataSourceRepository).existsById(dataSource.getId());
        verify(dataSourceRepository, never()).deleteById(any());
    }

    @Test
    void findDataSources_Success() {
        // 准备测试数据
        List<DataSource> dataSources = Arrays.asList(dataSource);
        Page<DataSource> page = new PageImpl<>(dataSources);

        // 设置模拟行为
        when(dataSourceRepository.findByConditions(
                dataSourceVO.getName(),
                dataSourceVO.getType(),
                DataSourceStatus.INACTIVE,
                Pageable.ofSize(10)
        )).thenReturn(page);

        // 执行测试
        Page<DataSource> result = dataSourceService.findDataSources(
                dataSourceVO.getName(),
                dataSourceVO.getType(),
                DataSourceStatus.INACTIVE,
                Pageable.ofSize(10)
        );

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(dataSource, result.getContent().get(0));
    }

    @Test
    void testDataSourceConnection_Success() {
        // 设置模拟行为
        when(dataSourceRepository.findById(dataSource.getId())).thenReturn(Optional.of(dataSource));
        when(dataSourceRepository.save(any(DataSource.class))).thenReturn(dataSource);

        // 执行测试
        boolean result = dataSourceService.testDataSourceConnection(dataSource.getId());

        // 验证结果
        assertFalse(result); // 由于无法实际连接数据库，预期为false
        verify(dataSourceRepository).findById(dataSource.getId());
        verify(dataSourceRepository).save(any(DataSource.class));
    }

    @Test
    void getDataSourceStatistics_Success() {
        // 设置模拟行为
        when(dataSourceRepository.count()).thenReturn(10L);
        when(dataSourceRepository.countByStatus(any())).thenReturn(2L);
        when(dataSourceRepository.countByType(any())).thenReturn(2L);

        // 执行测试
        DataSourceService.DataSourceStatistics stats = dataSourceService.getDataSourceStatistics();

        // 验证结果
        assertNotNull(stats);
        assertEquals(10L, stats.getTotalCount());
        assertEquals(2L, stats.getActiveCount());
        assertEquals(2L, stats.getRelationalCount());
        assertEquals(2L, stats.getBigDataCount());
        assertEquals(2L, stats.getAnalyticalCount());
    }
}