package com.datascope.infrastructure.repository.mapper;

import com.datascope.domain.model.datasource.DataSource;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 数据源Mapper接口
 */
@Mapper
public interface DataSourceMapper {
    
    /**
     * 插入数据源
     */
    @Insert({
        "INSERT INTO tbl_data_source (",
        "id, name, type, host, port, database, username, password_encrypted, salt,",
        "parameters, description, enabled, nonce, created_at, created_by, modified_at, modified_by",
        ") VALUES (",
        "#{id}, #{name}, #{type}, #{host}, #{port}, #{database}, #{username},",
        "#{passwordEncrypted}, #{salt}, #{parameters}, #{description}, #{enabled},",
        "#{nonce}, #{createdAt}, #{createdBy}, #{modifiedAt}, #{modifiedBy}",
        ")"
    })
    int insert(DataSource dataSource);
    
    /**
     * 更新数据源
     */
    @Update({
        "UPDATE tbl_data_source SET",
        "name = #{name},",
        "type = #{type},",
        "host = #{host},",
        "port = #{port},",
        "database = #{database},",
        "username = #{username},",
        "password_encrypted = #{passwordEncrypted},",
        "salt = #{salt},",
        "parameters = #{parameters},",
        "description = #{description},",
        "enabled = #{enabled},",
        "nonce = #{nonce} + 1,",
        "modified_at = #{modifiedAt},",
        "modified_by = #{modifiedBy}",
        "WHERE id = #{id} AND nonce = #{nonce}"
    })
    int update(DataSource dataSource);
    
    /**
     * 根据ID查询数据源
     */
    @Select("SELECT * FROM tbl_data_source WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "host", column = "host"),
        @Result(property = "port", column = "port"),
        @Result(property = "database", column = "database"),
        @Result(property = "username", column = "username"),
        @Result(property = "passwordEncrypted", column = "password_encrypted"),
        @Result(property = "salt", column = "salt"),
        @Result(property = "parameters", column = "parameters"),
        @Result(property = "description", column = "description"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    DataSource findById(String id);
    
    /**
     * 查询所有数据源
     */
    @Select("SELECT * FROM tbl_data_source")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "type", column = "type"),
        @Result(property = "host", column = "host"),
        @Result(property = "port", column = "port"),
        @Result(property = "database", column = "database"),
        @Result(property = "username", column = "username"),
        @Result(property = "passwordEncrypted", column = "password_encrypted"),
        @Result(property = "salt", column = "salt"),
        @Result(property = "parameters", column = "parameters"),
        @Result(property = "description", column = "description"),
        @Result(property = "enabled", column = "enabled"),
        @Result(property = "nonce", column = "nonce"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<DataSource> findAll();
    
    /**
     * 删除数据源
     */
    @Delete("DELETE FROM tbl_data_source WHERE id = #{id}")
    int deleteById(String id);
}