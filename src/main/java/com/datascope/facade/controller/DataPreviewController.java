package com.datascope.facade.controller;

import com.datascope.domain.model.preview.DataPreviewRequest;
import com.datascope.domain.model.preview.DataPreviewResponse;
import com.datascope.domain.service.preview.DataPreviewService;
import com.datascope.infrastructure.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 数据预览接口 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/preview")
@Tag(name = "数据预览", description = "数据预览相关接口")
public class DataPreviewController {

  private final DataPreviewService dataPreviewService;

  /**
   * 预览数据
   *
   * @param request 预览请求
   * @return 预览结果
   */
  @PostMapping
  @Operation(summary = "预览数据", description = "根据查询条件预览表数据")
  public R<DataPreviewResponse> preview(@Valid @RequestBody DataPreviewRequest request) {
    return R.ok(dataPreviewService.previewData(request));
  }
}
