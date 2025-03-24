package com.datascope.domain.model.lowcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 低代码配置VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowcodeConfigVO {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 查询配置ID
     */
    @NotBlank(message = "查询配置ID不能为空")
    private String queryConfigId;

    /**
     * 展示类型
     */
    @NotNull(message = "展示类型不能为空")
    private DisplayType displayType;

    /**
     * 配置详情(JSON)
     */
    @NotBlank(message = "配置详情不能为空")
    private String config;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 从实体转换为VO
     *
     * @param entity 实体
     * @return VO
     */
    public static LowcodeConfigVO fromEntity(LowcodeConfig entity) {
        if (entity == null) {
            return null;
        }
        return LowcodeConfigVO.builder()
                .id(entity.getId())
                .queryConfigId(entity.getQueryConfigId())
                .displayType(entity.getDisplayType())
                .config(entity.getConfig())
                .version(entity.getVersion())
                .build();
    }

    /**
     * 转换为实体
     *
     * @return 实体
     */
    public LowcodeConfig toEntity() {
        return LowcodeConfig.builder()
                .id(this.getId())
                .queryConfigId(this.getQueryConfigId())
                .displayType(this.getDisplayType())
                .config(this.getConfig())
                .version(this.getVersion() != null ? this.getVersion() : 1)
                .build();
    }
}