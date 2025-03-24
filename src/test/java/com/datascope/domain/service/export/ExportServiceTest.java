package com.datascope.domain.service.export;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.datascope.domain.model.export.ColumnConfig;
import com.datascope.domain.model.export.ExportConfigVO;
import com.datascope.domain.model.export.ExportHistoryVO;
import com.datascope.domain.model.export.ExportStatus;
import com.datascope.domain.model.export.ExportType;
import com.datascope.domain.model.export.FilterConfig;

/**
 * 导出服务单元测试
 */
public class ExportServiceTest {

    private ExportService exportService;

    @BeforeEach
    public void setup() {
        exportService = mock(ExportService.class);
    }

    @Test
    public void testCreateExportConfig() {
        // 准备测试数据
        ExportConfigVO config = new ExportConfigVO();
        config.setId("1");
        config.setName("测试导出");
        config.setQueryConfigId("123");
        config.setDataSourceId("456");
        config.setTableName("test_table");
        config.setExportType(ExportType.CSV);
        
        // 配置列
        List<ColumnConfig> columns = new ArrayList<>();
        ColumnConfig column = ColumnConfig.builder()
                .field("username")
                .label("用户名")
                .export(true)
                .build();
        columns.add(column);
        config.setColumnConfig(columns);
        
        // 过滤条件
        FilterConfig filter = new FilterConfig();
        // 设置filter属性
        config.setFilterConfig(filter);
        
        // 模拟service行为
        when(exportService.createExportConfig(any(ExportConfigVO.class))).thenReturn(config);
        
        // 测试
        ExportConfigVO result = exportService.createExportConfig(config);
        
        // 验证
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试导出", result.getName());
        assertEquals(ExportType.CSV, result.getExportType());
        assertEquals(1, result.getColumnConfig().size());
    }
    
    @Test
    public void testGetExportConfig() {
        // 准备测试数据
        ExportConfigVO config = new ExportConfigVO();
        config.setId("1");
        config.setName("测试导出");
        config.setQueryConfigId("123");
        config.setDataSourceId("456");
        
        // 模拟service行为
        when(exportService.getExportConfig("1")).thenReturn(config);
        
        // 测试
        ExportConfigVO result = exportService.getExportConfig("1");
        
        // 验证
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试导出", result.getName());
    }
    
    @Test
    public void testFindExportConfigs() {
        // 准备测试数据
        ExportConfigVO config1 = new ExportConfigVO();
        config1.setId("1");
        config1.setName("测试导出1");
        config1.setQueryConfigId("123");
        config1.setDataSourceId("456");
        
        ExportConfigVO config2 = new ExportConfigVO();
        config2.setId("2");
        config2.setName("测试导出2");
        config2.setQueryConfigId("789");
        config2.setDataSourceId("012");
        
        List<ExportConfigVO> configs = Arrays.asList(config1, config2);
        Page<ExportConfigVO> page = new PageImpl<>(configs);
        
        Pageable pageable = PageRequest.of(0, 10);
        
        // 模拟service行为
        when(exportService.findExportConfigs("456", "123", "test", pageable)).thenReturn(page);
        
        // 测试
        Page<ExportConfigVO> result = exportService.findExportConfigs("456", "123", "test", pageable);
        
        // 验证
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("测试导出1", result.getContent().get(0).getName());
    }
    
    @Test
    public void testExecuteExport() {
        // 准备测试数据
        ExportHistoryVO history = new ExportHistoryVO();
        history.setId("1");
        history.setExportConfigId("123");
        history.setConfigName("测试导出");
        history.setFileName("export_123.csv");
        history.setFileSize(1024L);
        history.setRowCount(100);
        history.setStartTime(LocalDateTime.now());
        history.setStatus(ExportStatus.COMPLETED);
        
        // 模拟service行为
        when(exportService.executeExport("123")).thenReturn(history);
        
        // 测试
        ExportHistoryVO result = exportService.executeExport("123");
        
        // 验证
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals(ExportStatus.COMPLETED, result.getStatus());
        assertEquals(100, result.getRowCount());
    }
}