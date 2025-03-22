import type { AxiosRequestConfig } from 'axios';
import type { ErrorHandlerConfig } from './error';

/**
 * 请求配置
 */
export interface RequestConfig extends AxiosRequestConfig {
  /** 是否显示加载状态 */
  showLoading?: boolean;
  /** 错误处理配置 */
  errorHandler?: ErrorHandlerConfig;
  /** 是否返回原始响应 */
  returnRaw?: boolean;
  /** 是否处理文件下载 */
  isDownload?: boolean;
  /** 是否启用请求节流 */
  enableThrottle?: boolean;
  /** 节流时间间隔(ms) */
  throttleInterval?: number;
}

/**
 * 请求拦截器配置
 */
export interface RequestInterceptorConfig {
  /** 请求拦截器 */
  onRequest?: (config: RequestConfig) => RequestConfig | Promise<RequestConfig>;
  /** 请求错误拦截器 */
  onRequestError?: (error: any) => any;
  /** 响应拦截器 */
  onResponse?: (response: any) => any;
  /** 响应错误拦截器 */
  onResponseError?: (error: any) => any;
}