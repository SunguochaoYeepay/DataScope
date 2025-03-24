package com.datascope.domain.service.query;

import com.datascope.domain.model.query.QueryConfig;
import com.datascope.domain.model.query.QueryHistory;
import com.datascope.domain.model.query.QueryRequest;
import com.datascope.domain.model.query.QueryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 查询服务接口
 */
public interface QueryService {

    /**
     * 创建查询配置
     *
     * @param queryConfig 查询配置
     * @return 创建后的查询配置
     */
    QueryConfig createQueryConfig(QueryConfig queryConfig);

    /**
     * 更新查询配置
     *
     * @param id 查询配置ID
     * @param queryConfig 查询配置
     * @return 更新后的查询配置
     */
    QueryConfig updateQueryConfig(String id, QueryConfig queryConfig);

    /**
     * 删除查询配置
     *
     * @param id 查询配置ID
     */
    void deleteQueryConfig(String id);

    /**
     * 获取查询配置
     *
     * @param id 查询配置ID
     * @return 查询配置
     */
    QueryConfig getQueryConfig(String id);

    /**
     * 分页查询查询配置
     *
     * @param name 名称（可选）
     * @param dataSourceId 数据源ID（可选）
     * @param pageable 分页参数
     * @return 查询配置分页结果
     */
    Page<QueryConfig> findQueryConfigs(String name, String dataSourceId, Pageable pageable);

    /**
     * 执行查询
     *
     * @param request 查询请求
     * @return 查询结果
     */
    QueryResult executeQuery(QueryRequest request);

    /**
     * 获取查询历史
     *
     * @param queryConfigId 查询配置ID
     * @param pageable 分页参数
     * @return 查询历史分页结果
     */
    Page<QueryHistory> getQueryHistory(String queryConfigId, Pageable pageable);

    /**
     * 获取最近执行的查询
     *
     * @param limit 限制数量
     * @return 查询历史列表
     */
    List<QueryHistory> getRecentQueries(int limit);
} 