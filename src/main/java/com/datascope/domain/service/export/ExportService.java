package com.datascope.domain.service.export;

import com.datascope.domain.model.export.ExportConfig;
import com.datascope.domain.model.export.ExportConfigVO;
import com.datascope.domain.model.export.ExportHistory;
import com.datascope.domain.model.export.ExportHistoryVO;
import com.datascope.domain.model.export.ExportStatus;
import com.datascope.domain.model.export.ExportType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 导出服务接口
 */
public interface ExportService {
    
    /**
     * 创建导出配置
     * @param exportConfigVO 导出配置信息
     * @return 创建的导出配置
     */
    ExportConfigVO createExportConfig(ExportConfigVO exportConfigVO);
    
    /**
     * 更新导出配置
     * @param id 配置ID
     * @param exportConfigVO 导出配置信息
     * @return 更新后的导出配置
     */
    ExportConfigVO updateExportConfig(String id, ExportConfigVO exportConfigVO);
    
    /**
     * 删除导出配置
     * @param id 配置ID
     */
    void deleteExportConfig(String id);
    
    /**
     * 获取导出配置详情
     * @param id 配置ID
     * @return 导出配置详情
     */
    ExportConfigVO getExportConfig(String id);
    
    /**
     * 获取所有导出配置
     * @return 导出配置列表
     */
    List<ExportConfigVO> getAllExportConfigs();
    
    /**
     * 分页查询导出配置
     * @param name 配置名称
     * @param dataSourceId 数据源ID
     * @param tableName 表名
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<ExportConfigVO> findExportConfigs(String name, String dataSourceId, String tableName, Pageable pageable);
    
    /**
     * 执行导出
     * @param configId 配置ID
     * @return 导出历史记录
     */
    ExportHistoryVO executeExport(String configId);
    
    /**
     * 获取导出历史记录
     * @param id 历史ID
     * @return 导出历史记录
     */
    ExportHistoryVO getExportHistory(String id);
    
    /**
     * 获取导出历史列表
     * @param configId 配置ID
     * @param status 状态
     * @param createdBy 创建人
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<ExportHistoryVO> findExportHistories(String configId, ExportStatus status, String createdBy, Pageable pageable);
    
    /**
     * 取消导出任务
     * @param id 历史ID
     * @return 更新后的历史记录
     */
    ExportHistoryVO cancelExport(String id);
    
    /**
     * 下载导出文件
     * @param id 历史ID
     * @return 文件字节数组
     */
    byte[] downloadExportFile(String id);
    
    /**
     * 删除导出历史记录
     * @param id 历史ID
     */
    void deleteExportHistory(String id);
    
    /**
     * 批量删除导出历史记录
     * @param ids 历史ID列表
     */
    void batchDeleteExportHistories(List<String> ids);
}