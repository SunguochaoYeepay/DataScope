package com.datascope.api.controller;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.common.model.PageRequest;
import com.datascope.common.model.PageResponse;
import com.datascope.common.model.Response;
import com.datascope.domain.service.QueryHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 查询历史控制器
 */
@Tag(name = "查询历史管理", description = "提供查询历史的查询、统计和管理功能")
@RestController
@RequestMapping("/api/v1/query-histories")
public class QueryHistoryController {

    private final QueryHistoryService queryHistoryService;

    public QueryHistoryController(QueryHistoryService queryHistoryService) {
        this.queryHistoryService = queryHistoryService;
    }

    @Operation(summary = "获取查询历史详情")
    @GetMapping("/{id}")
    public Response<QueryHistory> getHistoryById(
            @Parameter(description = "历史记录ID", required = true)
            @PathVariable String id) {
        return Response.success(queryHistoryService.getHistoryById(id));
    }

    @Operation(summary = "获取查询相关的历史记录")
    @GetMapping("/by-query/{queryId}")
    public Response<List<QueryHistory>> getHistoriesByQueryId(
            @Parameter(description = "查询ID", required = true)
            @PathVariable String queryId) {
        return Response.success(queryHistoryService.getHistoriesByQueryId(queryId));
    }

    @Operation(summary = "分页查询用户的查询历史")
    @GetMapping("/user/{userId}")
    public Response<PageResponse<QueryHistory>> getUserHistoryByPage(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId,
            
            @Parameter(description = "开始时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,
            
            @Parameter(description = "结束时间")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime,
            
            @Parameter(description = "状态")
            @RequestParam(required = false)
            String status,
            
            @Parameter(description = "页码")
            @RequestParam(defaultValue = "1")
            int pageNum,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10")
            int pageSize) {
        
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        return Response.success(queryHistoryService.getUserHistoryByPage(
            userId, startTime, endTime, status, pageRequest));
    }

    @Operation(summary = "获取用户最近的查询历史")
    @GetMapping("/user/{userId}/recent")
    public Response<List<QueryHistory>> getRecentHistories(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId,
            
            @Parameter(description = "限制数量")
            @RequestParam(defaultValue = "10")
            int limit) {
        return Response.success(queryHistoryService.getRecentHistories(userId, limit));
    }

    @Operation(summary = "删除历史记录")
    @DeleteMapping("/{id}")
    public Response<Void> deleteHistory(
            @Parameter(description = "历史记录ID", required = true)
            @PathVariable String id) {
        queryHistoryService.deleteHistory(id);
        return Response.success(null);
    }

    @Operation(summary = "删除查询相关的所有历史记录")
    @DeleteMapping("/by-query/{queryId}")
    public Response<Void> deleteHistoriesByQueryId(
            @Parameter(description = "查询ID", required = true)
            @PathVariable String queryId) {
        queryHistoryService.deleteHistoriesByQueryId(queryId);
        return Response.success(null);
    }

    @Operation(summary = "清理指定天数前的历史记录")
    @DeleteMapping("/clean")
    public Response<Integer> cleanHistoryBefore(
            @Parameter(description = "天数", required = true)
            @RequestParam int days) {
        return Response.success(queryHistoryService.cleanHistoryBefore(days));
    }

    @Operation(summary = "获取执行统计信息")
    @GetMapping("/stats")
    public Response<Map<String, Object>> getExecutionStats(
            @Parameter(description = "开始时间", required = true)
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,
            
            @Parameter(description = "结束时间", required = true)
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime,
            
            @Parameter(description = "用户ID")
            @RequestParam(required = false)
            String userId,
            
            @Parameter(description = "查询ID")
            @RequestParam(required = false)
            String queryId) {
        return Response.success(queryHistoryService.getExecutionStats(
            startTime, endTime, userId, queryId));
    }

    @Operation(summary = "获取最近失败的查询记录")
    @GetMapping("/failures")
    public Response<List<QueryHistory>> getRecentFailures(
            @Parameter(description = "用户ID")
            @RequestParam(required = false)
            String userId,
            
            @Parameter(description = "限制数量")
            @RequestParam(defaultValue = "10")
            int limit) {
        return Response.success(queryHistoryService.getRecentFailures(userId, limit));
    }

    @Operation(summary = "获取慢查询记录")
    @GetMapping("/slow-queries")
    public Response<List<QueryHistory>> getSlowQueries(
            @Parameter(description = "执行时间阈值（毫秒）", required = true)
            @RequestParam
            long thresholdMillis,
            
            @Parameter(description = "开始时间", required = true)
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startTime,
            
            @Parameter(description = "结束时间", required = true)
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endTime,
            
            @Parameter(description = "限制数量")
            @RequestParam(defaultValue = "10")
            int limit) {
        return Response.success(queryHistoryService.getSlowQueries(
            thresholdMillis, startTime, endTime, limit));
    }
}