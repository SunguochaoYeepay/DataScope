package com.datascope.infrastructure.repository.mapper;

import com.datascope.domain.model.query.QueryHistory;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 查询历史Mapper接口
 */
@Mapper
public interface QueryHistoryMapper {
    
    /**
     * 插入查询历史
     */
    @Insert({
        "INSERT INTO tbl_query_history (",
        "id, query_id, parameters, execution_time, affected_rows,",
        "status, error_message, execution_ip, created_at, created_by",
        ") VALUES (",
        "#{id}, #{queryId}, #{parameters}, #{executionTime},",
        "#{affectedRows}, #{status}, #{errorMessage}, #{executionIp},",
        "#{createdAt}, #{createdBy}",
        ")"
    })
    int insert(QueryHistory history);
    
    /**
     * 根据ID查询历史
     */
    @Select("SELECT * FROM tbl_query_history WHERE id = #{id}")
    @ResultMap("BaseResultMap")
    QueryHistory findById(String id);
    
    /**
     * 根据查询ID查询历史列表
     */
    @Select("SELECT * FROM tbl_query_history WHERE query_id = #{queryId} ORDER BY created_at DESC")
    @ResultMap("BaseResultMap")
    List<QueryHistory> findByQueryId(String queryId);
    
    /**
     * 根据用户ID查询历史列表
     */
    @Select("SELECT * FROM tbl_query_history WHERE created_by = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    @ResultMap("BaseResultMap")
    List<QueryHistory> findByUserId(@Param("userId") String userId, @Param("limit") int limit);
    
    /**
     * 删除查询历史
     */
    @Delete("DELETE FROM tbl_query_history WHERE id = #{id}")
    int deleteById(String id);
    
    /**
     * 删除查询相关的所有历史
     */
    @Delete("DELETE FROM tbl_query_history WHERE query_id = #{queryId}")
    int deleteByQueryId(String queryId);
    
    /**
     * 清理指定天数前的历史记录
     */
    @Delete("DELETE FROM tbl_query_history WHERE created_at < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int cleanHistoryBefore(int days);

    /**
     * 分页查询用户的查询历史
     */
    List<QueryHistory> findUserHistoryByPage(@Param("userId") String userId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("status") String status,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);

    /**
     * 统计用户的查询历史总数
     */
    Long countUserHistory(@Param("userId") String userId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime,
                        @Param("status") String status);

    /**
     * 批量插入查询历史
     */
    int batchInsert(List<QueryHistory> histories);

    /**
     * 查询指定时间范围内的执行统计
     */
    Map<String, Object> getExecutionStats(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime,
                                        @Param("userId") String userId,
                                        @Param("queryId") String queryId);

    /**
     * 查询最近失败的查询历史
     */
    List<QueryHistory> findRecentFailures(@Param("userId") String userId,
                                        @Param("limit") int limit);

    /**
     * 查询执行时间超过阈值的查询历史
     */
    List<QueryHistory> findSlowQueries(@Param("threshold") long threshold,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("limit") int limit);

    /**
     * 结果映射
     */
    @Results(id = "BaseResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "queryId", column = "query_id"),
        @Result(property = "parameters", column = "parameters"),
        @Result(property = "executionTime", column = "execution_time"),
        @Result(property = "affectedRows", column = "affected_rows"),
        @Result(property = "status", column = "status"),
        @Result(property = "errorMessage", column = "error_message"),
        @Result(property = "executionIp", column = "execution_ip"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by")
    })
    void baseResultMap();
}