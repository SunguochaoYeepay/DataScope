package com.datascope.infrastructure.repository.mapper;

import com.datascope.domain.model.query.Query;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 查询Mapper接口
 */
@Mapper
public interface QueryMapper {
    
    /**
     * 插入查询
     */
    @Insert({
        "INSERT INTO tbl_query (",
        "id, name, source_id, sql_text, description, version,",
        "status, timeout, favorite, tags, remark, nonce,",
        "created_at, created_by, modified_at, modified_by",
        ") VALUES (",
        "#{id}, #{name}, #{sourceId}, #{sqlText}, #{description},",
        "#{version}, #{status}, #{timeout}, #{favorite}, #{tags},",
        "#{remark}, #{nonce}, #{createdAt}, #{createdBy},",
        "#{modifiedAt}, #{modifiedBy}",
        ")"
    })
    int insert(Query query);
    
    /**
     * 更新查询
     */
    @Update({
        "UPDATE tbl_query SET",
        "name = #{name},",
        "sql_text = #{sqlText},",
        "description = #{description},",
        "version = #{version},",
        "status = #{status},",
        "timeout = #{timeout},",
        "favorite = #{favorite},",
        "tags = #{tags},",
        "remark = #{remark},",
        "nonce = #{nonce} + 1,",
        "modified_at = #{modifiedAt},",
        "modified_by = #{modifiedBy}",
        "WHERE id = #{id} AND nonce = #{nonce}"
    })
    int update(Query query);
    
    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM tbl_query WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "sourceId", column = "source_id"),
        @Result(property = "sqlText", column = "sql_text"),
        @Result(property = "description", column = "description"),
        @Result(property = "version", column = "version"),
        @Result(property = "status", column = "status"),
        @Result(property = "timeout", column = "timeout"),
        @Result(property = "favorite", column = "favorite"),
        @Result(property = "tags", column = "tags"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    Query findById(String id);
    
    /**
     * 根据数据源ID查询列表
     */
    @Select("SELECT * FROM tbl_query WHERE source_id = #{sourceId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "sourceId", column = "source_id"),
        @Result(property = "sqlText", column = "sql_text"),
        @Result(property = "description", column = "description"),
        @Result(property = "version", column = "version"),
        @Result(property = "status", column = "status"),
        @Result(property = "timeout", column = "timeout"),
        @Result(property = "favorite", column = "favorite"),
        @Result(property = "tags", column = "tags"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<Query> findBySourceId(String sourceId);
    
    /**
     * 查询收藏的查询
     */
    @Select("SELECT * FROM tbl_query WHERE favorite = true AND created_by = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "sourceId", column = "source_id"),
        @Result(property = "sqlText", column = "sql_text"),
        @Result(property = "description", column = "description"),
        @Result(property = "version", column = "version"),
        @Result(property = "status", column = "status"),
        @Result(property = "timeout", column = "timeout"),
        @Result(property = "favorite", column = "favorite"),
        @Result(property = "tags", column = "tags"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<Query> findFavorites(String userId);
    
    /**
     * 删除查询
     */
    @Delete("DELETE FROM tbl_query WHERE id = #{id}")
    int deleteById(String id);
}