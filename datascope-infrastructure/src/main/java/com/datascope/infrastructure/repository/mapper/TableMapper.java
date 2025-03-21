package com.datascope.infrastructure.repository.mapper;

import com.datascope.domain.model.metadata.Table;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 数据表Mapper接口
 */
@Mapper
public interface TableMapper {
    
    /**
     * 插入表
     */
    @Insert({
        "INSERT INTO tbl_meta_table (",
        "id, schema_id, name, type, engine, comment, estimated_rows,",
        "data_length, index_length, nonce, created_at, created_by,",
        "modified_at, modified_by",
        ") VALUES (",
        "#{id}, #{schemaId}, #{name}, #{type}, #{engine}, #{comment},",
        "#{estimatedRows}, #{dataLength}, #{indexLength}, #{nonce},",
        "#{createdAt}, #{createdBy}, #{modifiedAt}, #{modifiedBy}",
        ")"
    })
    int insert(Table table);
    
    /**
     * 更新表
     */
    @Update({
        "UPDATE tbl_meta_table SET",
        "name = #{name},",
        "type = #{type},",
        "engine = #{engine},",
        "comment = #{comment},",
        "estimated_rows = #{estimatedRows},",
        "data_length = #{dataLength},",
        "index_length = #{indexLength},",
        "nonce = #{nonce} + 1,",
        "modified_at = #{modifiedAt},",
        "modified_by = #{modifiedBy}",
        "WHERE id = #{id} AND nonce = #{nonce}"
    })
    int update(Table table);
    
    /**
     * 根据ID查询表
     */
    @Select("SELECT * FROM tbl_meta_table WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "schemaId", column = "schema_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "engine", column = "engine"),
        @Result(property = "comment", column = "comment"),
        @Result(property = "estimatedRows", column = "estimated_rows"),
        @Result(property = "dataLength", column = "data_length"),
        @Result(property = "indexLength", column = "index_length"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    Table findById(String id);
    
    /**
     * 根据模式ID查询表列表
     */
    @Select("SELECT * FROM tbl_meta_table WHERE schema_id = #{schemaId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "schemaId", column = "schema_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "engine", column = "engine"),
        @Result(property = "comment", column = "comment"),
        @Result(property = "estimatedRows", column = "estimated_rows"),
        @Result(property = "dataLength", column = "data_length"),
        @Result(property = "indexLength", column = "index_length"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<Table> findBySchemaId(String schemaId);
    
    /**
     * 删除表
     */
    @Delete("DELETE FROM tbl_meta_table WHERE id = #{id}")
    int deleteById(String id);
    
    /**
     * 删除模式下的所有表
     */
    @Delete("DELETE FROM tbl_meta_table WHERE schema_id = #{schemaId}")
    int deleteBySchemaId(String schemaId);
    
    /**
     * 批量插入表
     */
    @Insert({
        "<script>",
        "INSERT INTO tbl_meta_table (",
        "id, schema_id, name, type, engine, comment, estimated_rows,",
        "data_length, index_length, nonce, created_at, created_by,",
        "modified_at, modified_by",
        ") VALUES",
        "<foreach collection='tables' item='table' separator=','>",
        "(",
        "#{table.id}, #{table.schemaId}, #{table.name}, #{table.type},",
        "#{table.engine}, #{table.comment}, #{table.estimatedRows},",
        "#{table.dataLength}, #{table.indexLength}, #{table.nonce},",
        "#{table.createdAt}, #{table.createdBy}, #{table.modifiedAt},",
        "#{table.modifiedBy}",
        ")",
        "</foreach>",
        "</script>"
    })
    int batchInsert(@Param("tables") List<Table> tables);
}