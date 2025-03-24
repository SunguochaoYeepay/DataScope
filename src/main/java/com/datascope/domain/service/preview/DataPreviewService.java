package com.datascope.domain.service.preview;

import com.datascope.domain.model.preview.DataPreviewRequest;
import com.datascope.domain.model.preview.DataPreviewResponse;

public interface DataPreviewService {
  /**
   * 预览表数据
   *
   * @param request 预览请求
   * @return 预览响应
   */
  DataPreviewResponse previewData(DataPreviewRequest request);
}
