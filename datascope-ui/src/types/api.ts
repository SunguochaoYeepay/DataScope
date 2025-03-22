/**
 * API 响应数据结构
 */
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

/**
 * 分页请求参数
 */
export interface PaginationParams {
  pageSize: number;
  current: number;
}

/**
 * 分页响应数据
 */
export interface PaginationResult<T> {
  total: number;
  list: T[];
  pageSize: number;
  current: number;
}

/**
 * 通用查询参数
 */
export interface QueryParams extends Partial<PaginationParams> {
  keyword?: string;
  sortField?: string;
  sortOrder?: 'ascend' | 'descend';
  [key: string]: any;
}

/**
 * 文件下载响应
 */
export interface DownloadResponse {
  filename: string;
  blob: Blob;
}