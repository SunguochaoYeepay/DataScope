package com.datascope.infrastructure.repository.mapper;

import com.datascope.domain.model.metadata.Relationship;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 表关系Mapper接口
 */
@Mapper
public interface RelationshipMapper {
    
    /**
     * 插入关系
     */
    @Insert({
        "INSERT INTO tbl_relationship (",
        "id, source_table_id, source_column_id, target_table_id,",
        "target_column_id, type, name, confidence, description,",
        "confirmed, nonce, created_at, created_by, modified_at, modified_by",
        ") VALUES (",
        "#{id}, #{sourceTableId}, #{sourceColumnId}, #{targetTableId},",
        "#{targetColumnId}, #{type}, #{name}, #{confidence}, #{description},",
        "#{confirmed}, #{nonce}, #{createdAt}, #{createdBy}, #{modifiedAt},",
        "#{modifiedBy}",
        ")"
    })
    int insert(Relationship relationship);
    
    /**
     * 更新关系
     */
    @Update({
        "UPDATE tbl_relationship SET",
        "type = #{type},",
        "name = #{name},",
        "confidence = #{confidence},",
        "description = #{description},",
        "confirmed = #{confirmed},",
        "nonce = #{nonce} + 1,",
        "modified_at = #{modifiedAt},",
        "modified_by = #{modifiedBy}",
        "WHERE id = #{id} AND nonce = #{nonce}"
    })
    int update(Relationship relationship);
    
    /**
     * 根据ID查询关系
     */
    @Select("SELECT * FROM tbl_relationship WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "sourceTableId", column = "source_table_id"),
        @Result(property = "sourceColumnId", column = "source_column_id"),
        @Result(property = "targetTableId", column = "target_table_id"),
        @Result(property = "targetColumnId", column = "target_column_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "name", column = "name"),
        @Result(property = "confidence", column = "confidence"),
        @Result(property = "description", column = "description"),
        @Result(property = "confirmed", column = "confirmed"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    Relationship findById(String id);
    
    /**
     * 根据源表ID查询关系列表
     */
    @Select("SELECT * FROM tbl_relationship WHERE source_table_id = #{tableId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "sourceTableId", column = "source_table_id"),
        @Result(property = "sourceColumnId", column = "source_column_id"),
        @Result(property = "targetTableId", column = "target_table_id"),
        @Result(property = "targetColumnId", column = "target_column_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "name", column = "name"),
        @Result(property = "confidence", column = "confidence"),
        @Result(property = "description", column = "description"),
        @Result(property = "confirmed", column = "confirmed"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<Relationship> findBySourceTableId(String tableId);
    
    /**
     * 根据目标表ID查询关系列表
     */
    @Select("SELECT * FROM tbl_relationship WHERE target_table_id = #{tableId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "sourceTableId", column = "source_table_id"),
        @Result(property = "sourceColumnId", column = "source_column_id"),
        @Result(property = "targetTableId", column = "target_table_id"),
        @Result(property = "targetColumnId", column = "target_column_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "name", column = "name"),
        @Result(property = "confidence", column = "confidence"),
        @Result(property = "description", column = "description"),
        @Result(property = "confirmed", column = "confirmed"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<Relationship> findByTargetTableId(String tableId);
    
    /**
     * 删除关系
     */
    @Delete("DELETE FROM tbl_relationship WHERE id = #{id}")
    int deleteById(String id);
    
    /**
     * 删除表相关的所有关系
     */
    @Delete("DELETE FROM tbl_relationship WHERE source_table_id = #{tableId} OR target_table_id = #{tableId}")
    int deleteByTableId(String tableId);
    
    /**
     * 批量插入关系
     */
    @Insert({
        "<script>",
        "INSERT INTO tbl_relationship (",
        "id, source_table_id, source_column_id, target_table_id,",
        "target_column_id, type, name, confidence, description,",
        "confirmed, nonce, created_at, created_by, modified_at, modified_by",
        ") VALUES",
        "<foreach collection='relationships' item='rel' separator=','>",
        "(",
        "#{rel.id}, #{rel.sourceTableId}, #{rel.sourceColumnId},",
        "#{rel.targetTableId}, #{rel.targetColumnId}, #{rel.type},",
        "#{rel.name}, #{rel.confidence}, #{rel.description},",
        "#{rel.confirmed}, #{rel.nonce}, #{rel.createdAt},",
        "#{rel.createdBy}, #{rel.modifiedAt}, #{rel.modifiedBy}",
        ")",
        "</foreach>",
        "</script>"
    })
    int batchInsert(@Param("relationships") List<Relationship> relationships);
}