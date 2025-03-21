package com.datascope.infrastructure.repository.mapper;

import com.datascope.domain.model.metadata.Column;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 数据列Mapper接口
 */
@Mapper
public interface ColumnMapper {
    
    /**
     * 插入列
     */
    @Insert({
        "INSERT INTO tbl_meta_column (",
        "id, table_id, name, type, length, precision, scale,",
        "nullable, default_value, comment, is_primary_key,",
        "is_auto_increment, charset, collation, ordinal_position,",
        "nonce, created_at, created_by, modified_at, modified_by",
        ") VALUES (",
        "#{id}, #{tableId}, #{name}, #{type}, #{length}, #{precision},",
        "#{scale}, #{nullable}, #{defaultValue}, #{comment},",
        "#{isPrimaryKey}, #{isAutoIncrement}, #{charset}, #{collation},",
        "#{ordinalPosition}, #{nonce}, #{createdAt}, #{createdBy},",
        "#{modifiedAt}, #{modifiedBy}",
        ")"
    })
    int insert(Column column);
    
    /**
     * 更新列
     */
    @Update({
        "UPDATE tbl_meta_column SET",
        "name = #{name},",
        "type = #{type},",
        "length = #{length},",
        "precision = #{precision},",
        "scale = #{scale},",
        "nullable = #{nullable},",
        "default_value = #{defaultValue},",
        "comment = #{comment},",
        "is_primary_key = #{isPrimaryKey},",
        "is_auto_increment = #{isAutoIncrement},",
        "charset = #{charset},",
        "collation = #{collation},",
        "ordinal_position = #{ordinalPosition},",
        "nonce = #{nonce} + 1,",
        "modified_at = #{modifiedAt},",
        "modified_by = #{modifiedBy}",
        "WHERE id = #{id} AND nonce = #{nonce}"
    })
    int update(Column column);
    
    /**
     * 根据ID查询列
     */
    @Select("SELECT * FROM tbl_meta_column WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "tableId", column = "table_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "length", column = "length"),
        @Result(property = "precision", column = "precision"),
        @Result(property = "scale", column = "scale"),
        @Result(property = "nullable", column = "nullable"),
        @Result(property = "defaultValue", column = "default_value"),
        @Result(property = "comment", column = "comment"),
        @Result(property = "isPrimaryKey", column = "is_primary_key"),
        @Result(property = "isAutoIncrement", column = "is_auto_increment"),
        @Result(property = "charset", column = "charset"),
        @Result(property = "collation", column = "collation"),
        @Result(property = "ordinalPosition", column = "ordinal_position"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    Column findById(String id);
    
    /**
     * 根据表ID查询列列表
     */
    @Select("SELECT * FROM tbl_meta_column WHERE table_id = #{tableId} ORDER BY ordinal_position")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "tableId", column = "table_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "length", column = "length"),
        @Result(property = "precision", column = "precision"),
        @Result(property = "scale", column = "scale"),
        @Result(property = "nullable", column = "nullable"),
        @Result(property = "defaultValue", column = "default_value"),
        @Result(property = "comment", column = "comment"),
        @Result(property = "isPrimaryKey", column = "is_primary_key"),
        @Result(property = "isAutoIncrement", column = "is_auto_increment"),
        @Result(property = "charset", column = "charset"),
        @Result(property = "collation", column = "collation"),
        @Result(property = "ordinalPosition", column = "ordinal_position"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<Column> findByTableId(String tableId);
    
    /**
     * 删除列
     */
    @Delete("DELETE FROM tbl_meta_column WHERE id = #{id}")
    int deleteById(String id);
    
    /**
     * 删除表下的所有列
     */
    @Delete("DELETE FROM tbl_meta_column WHERE table_id = #{tableId}")
    int deleteByTableId(String tableId);
    
    /**
     * 批量插入列
     */
    @Insert({
        "<script>",
        "INSERT INTO tbl_meta_column (",
        "id, table_id, name, type, length, precision, scale,",
        "nullable, default_value, comment, is_primary_key,",
        "is_auto_increment, charset, collation, ordinal_position,",
        "nonce, created_at, created_by, modified_at, modified_by",
        ") VALUES",
        "<foreach collection='columns' item='column' separator=','>",
        "(",
        "#{column.id}, #{column.tableId}, #{column.name}, #{column.type},",
        "#{column.length}, #{column.precision}, #{column.scale},",
        "#{column.nullable}, #{column.defaultValue}, #{column.comment},",
        "#{column.isPrimaryKey}, #{column.isAutoIncrement}, #{column.charset},",
        "#{column.collation}, #{column.ordinalPosition}, #{column.nonce},",
        "#{column.createdAt}, #{column.createdBy}, #{column.modifiedAt},",
        "#{column.modifiedBy}",
        ")",
        "</foreach>",
        "</script>"
    })
    int batchInsert(@Param("columns") List<Column> columns);
}