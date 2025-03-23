package com.datascope.domain.model.datasource;

import com.datascope.domain.model.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "tbl_data_source")
public class DataSource extends BaseEntity {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataSourceType type;
    
    @Column(nullable = false)
    private String host;
    
    @Column(nullable = false)
    private Integer port;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String database;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataSourceStatus status = DataSourceStatus.INACTIVE;
    
    @ElementCollection
    @CollectionTable(name = "tbl_data_source_properties")
    @MapKeyColumn(name = "property_key")
    @Column(name = "property_value")
    private Map<String, String> properties = new HashMap<>();
    
    @Column(length = 1000)
    private String description;
    
    /**
     * 生成JDBC URL
     */
    public String generateJdbcUrl() {
        return switch (type) {
            case MYSQL -> String.format("jdbc:mysql://%s:%d/%s", host, port, database);
            case POSTGRESQL -> String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            case ORACLE -> String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, database);
            case SQLSERVER -> String.format("jdbc:sqlserver://%s:%d;databaseName=%s", host, port, database);
            case DB2 -> String.format("jdbc:db2://%s:%d/%s", host, port, database);
            case HIVE -> String.format("jdbc:hive2://%s:%d/%s", host, port, database);
            case CLICKHOUSE -> String.format("jdbc:clickhouse://%s:%d/%s", host, port, database);
            case DORIS -> String.format("jdbc:mysql://%s:%d/%s", host, port, database);
        };
    }
    
    /**
     * 获取驱动类名
     */
    public String getDriverClassName() {
        return switch (type) {
            case MYSQL, DORIS -> "com.mysql.cj.jdbc.Driver";
            case POSTGRESQL -> "org.postgresql.Driver";
            case ORACLE -> "oracle.jdbc.OracleDriver";
            case SQLSERVER -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case DB2 -> "com.ibm.db2.jcc.DB2Driver";
            case HIVE -> "org.apache.hive.jdbc.HiveDriver";
            case CLICKHOUSE -> "com.clickhouse.jdbc.ClickHouseDriver";
        };
    }
    
    /**
     * 测试连接
     */
    public boolean testConnection() {
        // 将在服务层实现具体逻辑
        return false;
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(DataSourceStatus newStatus) {
        this.status = newStatus;
    }
    
    /**
     * 添加属性
     */
    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }
    
    /**
     * 移除属性
     */
    public void removeProperty(String key) {
        this.properties.remove(key);
    }
}