package com.datascope.domain.model.datasource.vo;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceStatus;
import com.datascope.domain.model.datasource.DataSourceType;
import java.time.LocalDateTime;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

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
  private String databaseName;

  private DataSourceStatus status;

  private Map<String, String> properties;

  private String description;

  private String createdBy;

  private LocalDateTime createdAt;

  private String updatedBy;

  private LocalDateTime updatedAt;

  /** 将 VO 转换为实体 */
  public DataSource toEntity() {
    DataSource entity = new DataSource();
    entity.setId(this.id);
    entity.setName(this.name);
    entity.setType(this.type);
    entity.setHost(this.host);
    entity.setPort(this.port);
    entity.setUsername(this.username);
    entity.setPassword(this.password);
    entity.setDatabaseName(this.databaseName);
    entity.setStatus(this.status != null ? this.status : DataSourceStatus.INACTIVE);
    entity.setProperties(this.properties);
    entity.setDescription(this.description);
    return entity;
  }

  /** 将实体转换为 VO */
  public static DataSourceVO fromEntity(DataSource entity) {
    DataSourceVO vo = new DataSourceVO();
    vo.setId(entity.getId());
    vo.setName(entity.getName());
    vo.setType(entity.getType());
    vo.setHost(entity.getHost());
    vo.setPort(entity.getPort());
    vo.setUsername(entity.getUsername());
    vo.setPassword(entity.getPassword());
    vo.setDatabaseName(entity.getDatabaseName());
    vo.setStatus(entity.getStatus());
    vo.setProperties(entity.getProperties());
    vo.setDescription(entity.getDescription());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedAt(entity.getCreatedAt());
    vo.setUpdatedBy(entity.getUpdatedBy());
    vo.setUpdatedAt(entity.getUpdatedAt());
    return vo;
  }
}
