package com.datascope.infrastructure.mapper;

import com.datascope.domain.model.datasource.DataSource;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 数据源Mapper接口
 */
@Mapper
public interface DataSourceMapper {
    
    /**
     * 插入数据源
     */
    @Insert({
        "INSERT INTO data_source (id, name, type, host, port, database_name, username, password,",
        "parameters, description, created_by, updated_by)",
        "VALUES (#{id}, #{name}, #{type}, #{host}, #{port}, #{databaseName}, #{username}, #{password},",
        "#{parameters}, #{description}, #{createdBy}, #{updatedBy})"
    })
    void insert(DataSource dataSource);

    /**
     * 更新数据源
     */
    @Update({
        "UPDATE data_source",
        "SET name = #{name}, type = #{type}, host = #{host}, port = #{port},",
        "database_name = #{databaseName}, username = #{username}, password = #{password},",
        "parameters = #{parameters}, description = #{description}, updated_by = #{updatedBy}",
        "WHERE id = #{id}"
    })
    void update(DataSource dataSource);

    /**
     * 根据ID查询数据源
     */
    @Select("SELECT * FROM data_source WHERE id = #{id}")
    @Results({
        @Result(property = "databaseName", column = "database_name"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "updatedBy", column = "updated_by")
    })
    Optional<DataSource> findById(String id);

    /**
     * 查询所有数据源
     */
    @Select("SELECT * FROM data_source")
    @Results({
        @Result(property = "databaseName", column = "database_name"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "updatedBy", column = "updated_by")
    })
    List<DataSource> findAll();

    /**
     * 根据名称查询数据源
     */
    @Select("SELECT * FROM data_source WHERE name = #{name}")
    @Results({
        @Result(property = "databaseName", column = "database_name"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "updatedBy", column = "updated_by")
    })
    Optional<DataSource> findByName(String name);

    /**
     * 根据ID删除数据源
     */
    @Delete("DELETE FROM data_source WHERE id = #{id}")
    void deleteById(String id);

    /**
     * 检查数据源名称是否存在
     */
    @Select("SELECT COUNT(*) FROM data_source WHERE name = #{name}")
    boolean existsByName(String name);
}