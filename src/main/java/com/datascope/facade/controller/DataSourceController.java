package com.datascope.facade.controller;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.service.datasource.DataSourceService;
import com.datascope.infrastructure.common.response.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据源管理接口
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/datasources")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    /**
     * 创建数据源
     *
     * @param dataSource 数据源信息
     * @return 创建的数据源
     */
    @PostMapping
    public R<DataSource> create(@Valid @RequestBody DataSource dataSource) {
        return R.ok(dataSourceService.create(dataSource));
    }

    /**
     * 更新数据源
     *
     * @param id 数据源ID
     * @param dataSource 数据源信息
     * @return 更新后的数据源
     */
    @PutMapping("/{id}")
    public R<DataSource> update(@PathVariable("id") @NotNull Long id,
                               @Valid @RequestBody DataSource dataSource) {
        dataSource.setId(id);
        return R.ok(dataSourceService.update(dataSource));
    }

    /**
     * 删除数据源
     *
     * @param id 数据源ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable("id") @NotNull Long id) {
        dataSourceService.delete(id);
        return R.ok();
    }

    /**
     * 获取数据源详情
     *
     * @param id 数据源ID
     * @return 数据源详情
     */
    @GetMapping("/{id}")
    public R<DataSource> get(@PathVariable("id") @NotNull Long id) {
        return R.ok(dataSourceService.get(id));
    }

    /**
     * 获取数据源列表
     *
     * @return 数据源列表
     */
    @GetMapping
    public R<List<DataSource>> list() {
        return R.ok(dataSourceService.list());
    }

    /**
     * 测试数据源连接
     *
     * @param id 数据源ID
     * @return 测试结果
     */
    @PostMapping("/{id}/test")
    public R<Boolean> testConnection(@PathVariable("id") @NotNull Long id) {
        return R.ok(dataSourceService.testConnection(id));
    }
}