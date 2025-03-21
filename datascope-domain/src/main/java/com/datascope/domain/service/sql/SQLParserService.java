package com.datascope.domain.service.sql;

import com.datascope.domain.dto.sql.SQLParseRequest;
import com.datascope.domain.dto.sql.SQLParseResponse;
import com.datascope.domain.model.sql.SQLStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SQL解析服务
 */
@Service
@RequiredArgsConstructor
public class SQLParserService {
    
    private final SQLParser sqlParser;
    
    /**
     * 解析SQL
     *
     * @param request SQL解析请求
     * @return SQL解析响应
     */
    public SQLParseResponse parseSql(SQLParseRequest request) {
        // 解析SQL
        SQLStatement statement = sqlParser.parse(request.getSql());
        
        // 转换为响应对象
        return SQLParseResponse.fromStatement(statement);
    }
}