package com.datascope.domain.model.datasource.vo;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.DataSourceType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class DataSourceVO {
    
    private String id;
    
    @NotBlank(message = "数据源名称不能为空")
    private String name;
    
    @NotNull(message = "数据源类型不能为空")
    private DataSourceType type;
    
    @NotBlank(message = "主机地址不能为空")
    private String host;
    
    @NotNull(message = "端口号不能为空")
    private Integer port;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "数据库名不能为空")
    private String database;
    
    private DataSourceStatus status;
    
    private Map<String, String> properties;
    
    private String description;
    
    private String createdBy;
    
    private LocalDateTime createdTime;
    
    private String lastModifiedBy;
    
    private LocalDateTime lastModifiedTime;
    
    /**
     * 从实体类转换为VO
     */
    public static DataSourceVO fromEntity(DataSource entity) {
        if (entity == null) {
            return null;
        }
        
        DataSourceVO vo = new DataSourceVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setType(entity.getType());
        vo.setHost(entity.getHost());
        vo.setPort(entity.getPort());
        vo.setUsername(entity.getUsername());
        vo.setPassword(entity.getPassword());
        vo.setDatabase(entity.getDatabase());
        vo.setStatus(entity.getStatus());
        vo.setProperties(entity.getProperties());
        vo.setDescription(entity.getDescription());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedTime(entity.getCreatedTime());
        vo.setLastModifiedBy(entity.getLastModifiedBy());
        vo.setLastModifiedTime(entity.getLastModifiedTime());
        return vo;
    }
    
    /**
     * 转换为实体类
     */
    public DataSource toEntity() {
        DataSource entity = new DataSource();
        entity.setId(this.getId());
        entity.setName(this.getName());
        entity.setType(this.getType());
        entity.setHost(this.getHost());
        entity.setPort(this.getPort());
        entity.setUsername(this.getUsername());
        entity.setPassword(this.getPassword());
        entity.setDatabase(this.getDatabase());
        entity.setStatus(this.getStatus());
        entity.setProperties(this.getProperties());
        entity.setDescription(this.getDescription());
        return entity;
    }
}