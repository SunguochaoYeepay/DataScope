package com.datascope.infrastructure.repository.mapper;

import com.datascope.domain.model.metadata.Schema;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 数据库模式Mapper接口
 */
@Mapper
public interface SchemaMapper {
    
    /**
     * 插入模式
     */
    @Insert({
        "INSERT INTO tbl_meta_schema (",
        "id, source_id, name, charset, collation, description,",
        "nonce, created_at, created_by, modified_at, modified_by",
        ") VALUES (",
        "#{id}, #{sourceId}, #{name}, #{charset}, #{collation}, #{description},",
        "#{nonce}, #{createdAt}, #{createdBy}, #{modifiedAt}, #{modifiedBy}",
        ")"
    })
    int insert(Schema schema);
    
    /**
     * 更新模式
     */
    @Update({
        "UPDATE tbl_meta_schema SET",
        "name = #{name},",
        "charset = #{charset},",
        "collation = #{collation},",
        "description = #{description},",
        "nonce = #{nonce} + 1,",
        "modified_at = #{modifiedAt},",
        "modified_by = #{modifiedBy}",
        "WHERE id = #{id} AND nonce = #{nonce}"
    })
    int update(Schema schema);
    
    /**
     * 根据ID查询模式
     */
    @Select("SELECT * FROM tbl_meta_schema WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "sourceId", column = "source_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "charset", column = "charset"),
        @Result(property = "collation", column = "collation"),
        @Result(property = "description", column = "description"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    Schema findById(String id);
    
    /**
     * 根据数据源ID查询模式列表
     */
    @Select("SELECT * FROM tbl_meta_schema WHERE source_id = #{sourceId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "sourceId", column = "source_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "charset", column = "charset"),
        @Result(property = "collation", column = "collation"),
        @Result(property = "description", column = "description"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<Schema> findBySourceId(String sourceId);
    
    /**
     * 删除模式
     */
    @Delete("DELETE FROM tbl_meta_schema WHERE id = #{id}")
    int deleteById(String id);
    
    /**
     * 删除数据源下的所有模式
     */
    @Delete("DELETE FROM tbl_meta_schema WHERE source_id = #{sourceId}")
    int deleteBySourceId(String sourceId);
}