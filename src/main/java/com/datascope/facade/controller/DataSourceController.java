package com.datascope.facade.controller;

import com.datascope.infrastructure.common.response.R;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import com.datascope.domain.service.datasource.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 数据源管理接口 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/datasources")
@Tag(name = "数据源管理", description = "数据源的增删改查操作")
public class DataSourceController {

  private final DataSourceService dataSourceService;

  /**
   * 创建数据源
   *
   * @param dataSource 数据源信息
   * @return 创建的数据源
   */
  @PostMapping
  @Operation(summary = "创建数据源", description = "创建新的数据源连接")
  public R<DataSourceVO> create(@RequestBody @Validated DataSourceVO dataSource) {
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
  @Operation(summary = "更新数据源", description = "根据ID更新数据源信息")
  public R<DataSourceVO> update(
      @PathVariable String id, @RequestBody @Valid DataSourceVO dataSourceVO) {
    return R.ok(dataSourceService.update(id, dataSourceVO));
  }

  /**
   * 删除数据源
   *
   * @param id 数据源ID
   * @return 无
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "删除数据源", description = "根据ID删除数据源")
  public R<Void> delete(@PathVariable String id) {
    dataSourceService.delete(id);
    return R.ok();
  }

  /**
   * 获取数据源
   *
   * @param id 数据源ID
   * @return 数据源信息
   */
  @GetMapping("/{id}")
  @Operation(summary = "获取数据源", description = "根据ID获取数据源详情")
  public R<DataSourceVO> get(@PathVariable String id) {
    return R.ok(dataSourceService.get(id));
  }

  /**
   * 获取所有数据源
   *
   * @return 数据源列表
   */
  @GetMapping
  @Operation(summary = "获取所有数据源", description = "获取所有可用的数据源列表")
  public R<List<DataSourceVO>> list() {
    return R.ok(dataSourceService.list());
  }

  /**
   * 测试数据源连接
   *
   * @param id 数据源ID
   * @return 测试结果
   */
  @PostMapping("/{id}/test")
  @Operation(summary = "测试数据源连接", description = "测试数据源连接是否正常")
  public R<Boolean> testConnection(@PathVariable String id) {
    return R.ok(dataSourceService.testDataSourceConnection(id));
  }
}
