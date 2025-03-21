package com.datascope.infrastructure.mapper;

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
     *
     * @param dataSource 数据源信息
     * @return 影响行数
     */
    @Insert({
        "INSERT INTO tbl_data_source (",
        "id, name, type, host, port, database_name, username, password,",
        "parameters, description, created_by, modified_by",
        ") VALUES (",
        "#{id}, #{name}, #{type}, #{host}, #{port}, #{databaseName}, #{username}, #{password},",
        "#{parameters}, #{description}, #{createdBy}, #{modifiedBy}",
        ")"
    })
    int insert(DataSource dataSource);

    /**
     * 更新数据源
     *
     * @param dataSource 数据源信息
     * @return 影响行数
     */
    @Update({
        "UPDATE tbl_data_source SET",
        "name = #{name},",
        "type = #{type},",
        "host = #{host},",
        "port = #{port},",
        "database_name = #{databaseName},",
        "username = #{username},",
        "password = #{password},",
        "parameters = #{parameters},",
        "description = #{description},",
        "modified_by = #{modifiedBy}",
        "WHERE id = #{id}"
    })
    int update(DataSource dataSource);

    /**
     * 根据ID查询数据源
     *
     * @param id 数据源ID
     * @return 数据源信息
     */
    @Select("SELECT * FROM tbl_data_source WHERE id = #{id}")
    @Results({
        @Result(property = "databaseName", column = "database_name"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    DataSource findById(String id);

    /**
     * 查询所有数据源
     *
     * @return 数据源列表
     */
    @Select("SELECT * FROM tbl_data_source")
    @Results({
        @Result(property = "databaseName", column = "database_name"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    List<DataSource> findAll();

    /**
     * 根据名称查询数据源
     *
     * @param name 数据源名称
     * @return 数据源信息
     */
    @Select("SELECT * FROM tbl_data_source WHERE name = #{name}")
    @Results({
        @Result(property = "databaseName", column = "database_name"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "modifiedAt", column = "modified_at"),
        @Result(property = "createdBy", column = "created_by"),
        @Result(property = "modifiedBy", column = "modified_by")
    })
    DataSource findByName(String name);

    /**
     * 根据ID删除数据源
     *
     * @param id 数据源ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tbl_data_source WHERE id = #{id}")
    int deleteById(String id);

    /**
     * 检查数据源名称是否存在
     *
     * @param name 数据源名称
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) FROM tbl_data_source WHERE name = #{name}")
    boolean existsByName(String name);
}