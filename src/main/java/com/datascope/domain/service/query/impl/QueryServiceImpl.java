package com.datascope.domain.service.query.impl;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.query.QueryConfig;
import com.datascope.domain.model.query.QueryHistory;
import com.datascope.domain.model.query.QueryRequest;
import com.datascope.domain.model.query.QueryResult;
import com.datascope.domain.model.query.QueryResultColumn;
import com.datascope.domain.model.query.QueryStatus;
import com.datascope.domain.repository.query.QueryConfigRepository;
import com.datascope.domain.repository.query.QueryHistoryRepository;
import com.datascope.domain.service.datasource.DataSourceService;
import com.datascope.domain.service.query.QueryService;
import com.datascope.infrastructure.common.exception.DataScopeException;
import com.datascope.infrastructure.common.exception.ErrorCode;
import com.datascope.infrastructure.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 查询服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final QueryConfigRepository queryConfigRepository;
    private final QueryHistoryRepository queryHistoryRepository;
    private final DataSourceService dataSourceService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public QueryConfig createQueryConfig(QueryConfig queryConfig) {
        // 检查名称是否重复
        if (queryConfigRepository.existsByName(queryConfig.getName())) {
            throw new DataScopeException(ErrorCode.DUPLICATE_NAME, "查询配置名称已存在");
        }

        // 设置ID
        queryConfig.setId(UUID.randomUUID().toString());
        
        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        queryConfig.setCreatedAt(now);
        queryConfig.setUpdatedAt(now);

        return queryConfigRepository.save(queryConfig);
    }

    @Override
    @Transactional
    public QueryConfig updateQueryConfig(String id, QueryConfig queryConfig) {
        QueryConfig existingConfig = queryConfigRepository.findById(id)
                .orElseThrow(() -> new DataScopeException(ErrorCode.RESOURCE_NOT_FOUND, "查询配置不存在"));

        // 检查名称是否重复
        if (!existingConfig.getName().equals(queryConfig.getName()) && 
                queryConfigRepository.existsByName(queryConfig.getName())) {
            throw new DataScopeException(ErrorCode.DUPLICATE_NAME, "查询配置名称已存在");
        }

        // 更新属性
        existingConfig.setName(queryConfig.getName());
        existingConfig.setDescription(queryConfig.getDescription());
        existingConfig.setSqlTemplate(queryConfig.getSqlTemplate());
        existingConfig.setParameters(queryConfig.getParameters());
        existingConfig.setTimeout(queryConfig.getTimeout());
        existingConfig.setMaxRows(queryConfig.getMaxRows());
        existingConfig.setUpdatedAt(LocalDateTime.now());
        existingConfig.setUpdatedBy(queryConfig.getUpdatedBy());

        return queryConfigRepository.save(existingConfig);
    }

    @Override
    @Transactional
    public void deleteQueryConfig(String id) {
        if (!queryConfigRepository.existsById(id)) {
            throw new DataScopeException(ErrorCode.RESOURCE_NOT_FOUND, "查询配置不存在");
        }
        queryConfigRepository.deleteById(id);
    }

    @Override
    public QueryConfig getQueryConfig(String id) {
        return queryConfigRepository.findById(id)
                .orElseThrow(() -> new DataScopeException(ErrorCode.RESOURCE_NOT_FOUND, "查询配置不存在"));
    }

    @Override
    public Page<QueryConfig> findQueryConfigs(String name, String dataSourceId, Pageable pageable) {
        return queryConfigRepository.findByConditions(name, dataSourceId, pageable);
    }

    @Override
    @Transactional
    public QueryResult executeQuery(QueryRequest request) {
        String queryConfigId = request.getQueryConfigId();
        
        // 验证查询配置ID
        if (queryConfigId == null || queryConfigId.trim().isEmpty()) {
            throw new DataScopeException(ErrorCode.INVALID_PARAMETER, "查询配置ID不能为空");
        }
        
        // 获取查询配置
        QueryConfig queryConfig = queryConfigRepository.findById(queryConfigId)
                .orElseThrow(() -> new DataScopeException(ErrorCode.RESOURCE_NOT_FOUND, "查询配置不存在"));
        
        // 创建查询历史记录
        QueryHistory history = createQueryHistory(request, queryConfig);
        
        try {
            // 开始计时
            long startTime = System.currentTimeMillis();
            
            // 准备SQL语句（替换参数）
            String sqlTemplate = queryConfig.getSqlTemplate();
            String sql = SqlUtils.renderSqlTemplate(sqlTemplate, request.getParameters());
            
            // 添加分页
            sql = SqlUtils.applyPagination(sql, request.getPagination());
            
            // 执行查询
            QueryResult result = executeQueryInternal(queryConfig.getDataSourceId(), sql, queryConfig.getMaxRows());
            
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            result.setExecutionTime(executionTime);
            
            // 更新查询历史
            updateQueryHistorySuccess(history, result, executionTime);
            
            return result;
        } catch (Exception e) {
            log.error("执行查询失败: {}", e.getMessage(), e);
            
            // 更新查询历史为失败状态
            updateQueryHistoryFailed(history, e.getMessage());
            
            // 抛出异常
            throw new DataScopeException(ErrorCode.QUERY_EXECUTION_FAILED, "执行查询失败: " + e.getMessage());
        }
    }

    @Override
    public Page<QueryHistory> getQueryHistory(String queryConfigId, Pageable pageable) {
        return queryHistoryRepository.findByQueryConfigId(queryConfigId, pageable);
    }

    @Override
    public List<QueryHistory> getRecentQueries(int limit) {
        // 获取当前用户
        String username = "admin"; // 暂时硬编码，后续从安全上下文获取
        
        // 根据用户名查询最近执行的查询
        return queryHistoryRepository.findTop10ByCreatedByOrderByStartTimeDesc(username)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 记录查询历史
     */
    private QueryHistory createQueryHistory(QueryRequest request, QueryConfig queryConfig) {
        String username = "admin"; // 暂时硬编码，后续从安全上下文获取
        
        QueryHistory history = new QueryHistory();
        history.setId(UUID.randomUUID().toString());
        history.setQueryConfigId(queryConfig.getId());
        try {
            history.setParameters(request.getParameters() != null ? 
                objectMapper.writeValueAsString(request.getParameters()) : null);
        } catch (Exception e) {
            log.warn("序列化查询参数失败", e);
            history.setParameters(request.getParameters() != null ? 
                request.getParameters().toString() : null);
        }
        history.setStartTime(LocalDateTime.now());
        history.setStatus(QueryStatus.RUNNING);
        history.setCreatedBy(username);
        
        return queryHistoryRepository.save(history);
    }
    
    /**
     * 更新查询历史为成功状态
     */
    private void updateQueryHistorySuccess(QueryHistory history, QueryResult result, long executionTime) {
        history.setEndTime(LocalDateTime.now());
        history.setDurationMs(executionTime);
        history.setStatus(QueryStatus.COMPLETED);
        history.setRowCount(result.getRows().size());
        
        queryHistoryRepository.save(history);
    }
    
    /**
     * 更新查询历史为失败状态
     */
    private void updateQueryHistoryFailed(QueryHistory history, String errorMessage) {
        history.setEndTime(LocalDateTime.now());
        history.setStatus(QueryStatus.FAILED);
        history.setErrorMessage(errorMessage);
        
        queryHistoryRepository.save(history);
    }
    
    /**
     * 执行查询
     */
    private QueryResult executeQueryInternal(String dataSourceId, String sql, int maxRows) throws SQLException {
        // 获取DataSource对象
        DataSource dataSource = dataSourceService.get(dataSourceId).toEntity();
        
        try (Connection connection = dataSourceService.getConnection(dataSource);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            // 设置最大返回行数
            statement.setMaxRows(maxRows);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                return processResultSet(resultSet, maxRows);
            }
        }
    }
    
    /**
     * 处理结果集
     */
    private QueryResult processResultSet(ResultSet resultSet, int maxRows) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // 创建列定义
        List<QueryResultColumn> columns = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            QueryResultColumn column = new QueryResultColumn();
            column.setName(metaData.getColumnName(i));
            column.setLabel(metaData.getColumnLabel(i));
            column.setType(metaData.getColumnTypeName(i));
            column.setSortable(true); // 暂时默认所有列都可排序
            
            columns.add(column);
        }
        
        // 创建数据行
        List<Map<String, Object>> rows = new ArrayList<>();
        boolean truncated = false;
        
        while (resultSet.next()) {
            if (rows.size() >= maxRows) {
                truncated = true;
                break;
            }
            
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                row.put(columnName, value);
            }
            
            rows.add(row);
        }
        
        // 构建查询结果
        return QueryResult.builder()
                .columns(columns)
                .rows(rows)
                .total((long) rows.size()) // 实际行数，如果需要总数，可能需要额外查询COUNT
                .truncated(truncated)
                .status(QueryStatus.COMPLETED)
                .build();
    }
} 