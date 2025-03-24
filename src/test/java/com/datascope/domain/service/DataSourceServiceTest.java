package com.datascope.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import com.datascope.domain.repository.DataSourceRepository;
import com.datascope.domain.service.impl.DataSourceServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataSourceServiceTest {

  @Mock private DataSourceRepository dataSourceRepository;

  @InjectMocks private DataSourceServiceImpl dataSourceService;

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
    dataSourceVO.setDatabaseName("test");

    dataSource = dataSourceVO.toEntity();
    dataSource.setId("1");
    dataSource.setStatus(DataSourceStatus.INACTIVE);
  }

  @Test
  void createDataSource_Success() {
    // 设置模拟行为
    when(dataSourceRepository.existsByName(dataSourceVO.getName())).thenReturn(false);
    when(dataSourceRepository.save(any(DataSource.class))).thenReturn(dataSource);

    // 执行测试
    DataSourceVO result = dataSourceService.create(dataSourceVO);

    // 验证结果
    assertNotNull(result);
    assertEquals(dataSourceVO.getName(), result.getName());
    verify(dataSourceRepository).existsByName(dataSourceVO.getName());
    verify(dataSourceRepository).save(any(DataSource.class));
  }

  @Test
  void createDataSource_NameExists() {
    // 设置模拟行为
    when(dataSourceRepository.existsByName(dataSourceVO.getName())).thenReturn(true);

    // 执行测试并验证异常
    assertThrows(IllegalArgumentException.class, () -> dataSourceService.create(dataSourceVO));

    verify(dataSourceRepository).existsByName(dataSourceVO.getName());
    verify(dataSourceRepository, never()).save(any(DataSource.class));
  }

  @Test
  void updateDataSource_Success() {
    // 设置模拟行为
    when(dataSourceRepository.findById(dataSource.getId())).thenReturn(Optional.of(dataSource));
    when(dataSourceRepository.save(any(DataSource.class))).thenReturn(dataSource);

    // 执行测试
    DataSourceVO result = dataSourceService.update(dataSource.getId(), dataSourceVO);

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
    assertThrows(
        EntityNotFoundException.class,
        () -> dataSourceService.update(dataSource.getId(), dataSourceVO));

    verify(dataSourceRepository).findById(dataSource.getId());
    verify(dataSourceRepository, never()).save(any(DataSource.class));
  }

  @Test
  void deleteDataSource_Success() {
    // 设置模拟行为
    when(dataSourceRepository.existsById(dataSource.getId())).thenReturn(true);

    // 执行测试
    dataSourceService.delete(dataSource.getId());

    // 验证结果
    verify(dataSourceRepository).existsById(dataSource.getId());
    verify(dataSourceRepository).deleteById(dataSource.getId());
  }

  @Test
  void deleteDataSource_NotFound() {
    // 设置模拟行为
    when(dataSourceRepository.existsById(dataSource.getId())).thenReturn(false);

    // 执行测试并验证异常
    assertThrows(EntityNotFoundException.class, () -> dataSourceService.delete(dataSource.getId()));

    verify(dataSourceRepository).existsById(dataSource.getId());
    verify(dataSourceRepository, never()).deleteById(any());
  }

  @Test
  void listDataSources_Success() {
    // 准备测试数据
    List<DataSource> dataSources = Arrays.asList(dataSource);

    // 设置模拟行为
    when(dataSourceRepository.findAll()).thenReturn(dataSources);

    // 执行测试
    List<DataSourceVO> result = dataSourceService.list();

    // 验证结果
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(dataSourceVO.getName(), result.get(0).getName());
  }

  @Test
  void testDataSourceConnection_Success() {
    // 设置模拟行为
    when(dataSourceRepository.findById(dataSource.getId())).thenReturn(Optional.of(dataSource));
    when(dataSourceRepository.save(any(DataSource.class))).thenReturn(dataSource);

    // 执行测试
    Boolean result = dataSourceService.testDataSourceConnection(dataSource.getId());

    // 验证结果
    assertFalse(result); // 由于无法实际连接数据库，预期为false
    verify(dataSourceRepository).findById(dataSource.getId());
    verify(dataSourceRepository).save(any(DataSource.class));
  }
}
