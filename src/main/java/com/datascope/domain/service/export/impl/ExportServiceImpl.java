package com.datascope.domain.service.export.impl;

import com.datascope.domain.model.export.*;
import com.datascope.domain.service.export.ExportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 导出服务实现类
 */
@Service
public class ExportServiceImpl implements ExportService {

    // 模拟数据
    private List<ExportConfigVO> configs = new ArrayList<>();
    private List<ExportHistoryVO> histories = new ArrayList<>();

    @Override
    public ExportConfigVO createExportConfig(ExportConfigVO exportConfigVO) {
        // 模拟数据
        exportConfigVO.setId(UUID.randomUUID().toString());
        configs.add(exportConfigVO);
        return exportConfigVO;
    }

    @Override
    public ExportConfigVO updateExportConfig(String id, ExportConfigVO exportConfigVO) {
        // 模拟更新逻辑
        exportConfigVO.setId(id);
        return exportConfigVO;
    }

    @Override
    public void deleteExportConfig(String id) {
        // 模拟删除逻辑
        configs.removeIf(config -> config.getId().equals(id));
    }

    @Override
    public ExportConfigVO getExportConfig(String id) {
        // 模拟查询逻辑
        return configs.stream()
                .filter(config -> config.getId().equals(id))
                .findFirst()
                .orElse(ExportConfigVO.builder()
                        .id(id)
                        .name("示例导出配置")
                        .dataSourceId("1")
                        .tableName("users")
                        .exportType(ExportType.CSV)
                        .build());
    }

    @Override
    public List<ExportConfigVO> getAllExportConfigs() {
        // 如果没有数据，返回模拟数据
        if (configs.isEmpty()) {
            return List.of(
                    ExportConfigVO.builder().id("1").name("用户数据导出").dataSourceId("1").tableName("users").exportType(ExportType.CSV).build(),
                    ExportConfigVO.builder().id("2").name("订单数据导出").dataSourceId("1").tableName("orders").exportType(ExportType.EXCEL).build()
            );
        }
        return configs;
    }

    @Override
    public Page<ExportConfigVO> findExportConfigs(String name, String dataSourceId, String tableName, Pageable pageable) {
        // 模拟分页查询
        List<ExportConfigVO> result = new ArrayList<>(getAllExportConfigs());
        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public ExportHistoryVO executeExport(String configId) {
        // 模拟执行导出
        ExportHistoryVO history = ExportHistoryVO.builder()
                .id(UUID.randomUUID().toString())
                .exportConfigId(configId)
                .configName("示例导出配置")
                .fileName("export_data.csv")
                .startTime(LocalDateTime.now())
                .status(ExportStatus.PROCESSING)
                .build();
        
        histories.add(history);
        return history;
    }

    @Override
    public ExportHistoryVO getExportHistory(String id) {
        // 模拟查询历史
        return histories.stream()
                .filter(history -> history.getId().equals(id))
                .findFirst()
                .orElse(ExportHistoryVO.builder()
                        .id(id)
                        .exportConfigId("1")
                        .configName("示例导出配置")
                        .fileName("export_data.csv")
                        .fileSize(1024L)
                        .rowCount(100)
                        .startTime(LocalDateTime.now().minusHours(1))
                        .endTime(LocalDateTime.now())
                        .status(ExportStatus.COMPLETED)
                        .build());
    }

    @Override
    public Page<ExportHistoryVO> findExportHistories(String configId, ExportStatus status, String createdBy, Pageable pageable) {
        // 模拟查询历史记录
        List<ExportHistoryVO> result = new ArrayList<>();
        if (histories.isEmpty()) {
            // 添加一些模拟数据
            result.add(ExportHistoryVO.builder()
                    .id(UUID.randomUUID().toString())
                    .exportConfigId("1")
                    .configName("用户数据导出")
                    .fileName("users_export.csv")
                    .fileSize(2048L)
                    .rowCount(200)
                    .startTime(LocalDateTime.now().minusHours(2))
                    .endTime(LocalDateTime.now().minusHours(1))
                    .status(ExportStatus.COMPLETED)
                    .createdBy("admin")
                    .build());
        } else {
            result.addAll(histories);
        }
        
        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public ExportHistoryVO cancelExport(String id) {
        // 模拟取消导出
        ExportHistoryVO history = getExportHistory(id);
        history.setStatus(ExportStatus.CANCELLED);
        history.setEndTime(LocalDateTime.now());
        return history;
    }

    @Override
    public byte[] downloadExportFile(String id) {
        // 模拟下载文件
        return "id,name,email\n1,测试用户,test@example.com".getBytes();
    }

    @Override
    public void deleteExportHistory(String id) {
        // 模拟删除历史
        histories.removeIf(history -> history.getId().equals(id));
    }

    @Override
    public void batchDeleteExportHistories(List<String> ids) {
        // 模拟批量删除
        histories.removeIf(history -> ids.contains(history.getId()));
    }
}