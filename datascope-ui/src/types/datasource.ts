/**
 * 数据源类型
 */
export type DataSourceType = 'MySQL' | 'DB2';

/**
 * 数据源状态
 */
export type DataSourceStatus = 'active' | 'inactive' | 'error';

/**
 * 数据源信息
 */
export interface DataSource {
  id: string;
  name: string;
  type: DataSourceType;
  host: string;
  port: number;
  database: string;
  username: string;
  status: DataSourceStatus;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * 数据源创建参数
 */
export interface CreateDataSourceParams {
  name: string;
  type: DataSourceType;
  host: string;
  port: number;
  database: string;
  username: string;
  password: string;
  description?: string;
}

/**
 * 数据源更新参数
 */
export interface UpdateDataSourceParams extends Partial<CreateDataSourceParams> {
  id: string;
}

/**
 * 数据源查询参数
 */
export interface DataSourceQueryParams {
  keyword?: string;
  type?: DataSourceType;
  status?: DataSourceStatus;
  pageSize?: number;
  current?: number;
}

/**
 * 数据源测试结果
 */
export interface TestConnectionResult {
  success: boolean;
  message: string;
  details?: string;
}