package com.datascope.controller;

import com.datascope.domain.dto.sql.SQLParseRequest;
import com.datascope.domain.dto.sql.SQLParseResponse;
import com.datascope.domain.service.sql.SQLParserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * SQL解析器接口
 */
@Tag(name = "SQL解析器", description = "SQL解析相关接口")
@RestController
@RequestMapping("/api/v1/sql-parser")
@RequiredArgsConstructor
public class SQLParserController {
    
    private final SQLParserService sqlParserService;
    
    /**
     * 解析SQL
     *
     * @param request SQL解析请求
     * @return SQL解析响应
     */
    @Operation(summary = "解析SQL", description = "解析SQL语句，返回解析结果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "解析成功"),
        @ApiResponse(responseCode = "400", description = "SQL语句格式错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/parse")
    public SQLParseResponse parseSql(
            @Parameter(description = "SQL解析请求", required = true)
            @RequestBody @Valid SQLParseRequest request) {
        return sqlParserService.parseSql(request);
    }
}