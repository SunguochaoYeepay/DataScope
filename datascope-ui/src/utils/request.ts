import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { message } from 'ant-design-vue';
import type { RequestConfig, RequestInterceptorConfig } from '../types/request';
import type { ApiResponse, DownloadResponse } from '../types/api';
import { ApiError, ErrorCode } from '../types/error';
import { throttle } from './throttle';

/**
 * 默认配置
 */
const DEFAULT_CONFIG: RequestConfig = {
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
  showLoading: true,
  errorHandler: {
    showErrorMessage: true,
  },
};

/**
 * 创建请求实例
 */
class Request {
  private instance: AxiosInstance;
  private loadingCount: number = 0;

  constructor(config: RequestConfig = {}) {
    this.instance = axios.create({
      ...DEFAULT_CONFIG,
      ...config,
    });
    this.setupInterceptors();
  }

  /**
   * 设置拦截器
   */
  private setupInterceptors(config: RequestInterceptorConfig = {}) {
    const {
      onRequest,
      onRequestError,
      onResponse,
      onResponseError,
    } = config;

    // 请求拦截
    this.instance.interceptors.request.use(
      async (config: RequestConfig) => {
        // 显示加载状态
        if (config.showLoading) {
          this.loadingCount++;
          // TODO: 显示全局loading
        }

        // 自定义请求处理
        if (onRequest) {
          config = await onRequest(config);
        }

        return config;
      },
      error => {
        if (onRequestError) {
          return onRequestError(error);
        }
        return Promise.reject(error);
      }
    );

    // 响应拦截
    this.instance.interceptors.response.use(
      async response => {
        // 隐藏加载状态
        if (response.config.showLoading) {
          this.loadingCount--;
          if (this.loadingCount === 0) {
            // TODO: 隐藏全局loading
          }
        }

        // 自定义响应处理
        if (onResponse) {
          response = await onResponse(response);
        }

        // 处理文件下载
        if (response.config.isDownload) {
          const contentDisposition = response.headers['content-disposition'];
          const filename = contentDisposition
            ? decodeURIComponent(contentDisposition.split('filename=')[1])
            : 'download';
          return {
            filename,
            blob: new Blob([response.data]),
          } as DownloadResponse;
        }

        // 处理普通响应
        const { data } = response;
        if (data.code !== ErrorCode.SUCCESS) {
          throw new ApiError(data.code, data.message, data.data);
        }

        return response.config.returnRaw ? response : data.data;
      },
      error => {
        // 隐藏加载状态
        if (error.config?.showLoading) {
          this.loadingCount--;
          if (this.loadingCount === 0) {
            // TODO: 隐藏全局loading
          }
        }

        // 自定义错误处理
        if (onResponseError) {
          return onResponseError(error);
        }

        // 处理请求超时
        if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
          error = new ApiError(ErrorCode.TIMEOUT, '请求超时');
        }

        // 处理网络错误
        if (!error.response) {
          error = new ApiError(
            ErrorCode.INTERNAL_ERROR, 
            `网络异常: ${error.message || '无法连接到服务器'}`
          );
        }

        // 处理HTTP错误
        if (error.response) {
          const { status, data, config } = error.response;
          error = new ApiError(
            status,
            `请求失败(${status}): ${data?.message || '服务器错误'} - ${config.url}`,
            data?.data
          );
        }

        return Promise.reject(error);
      }
    );
  }

  /**
   * 处理错误
   */
  private handleError(error: any, config: RequestConfig) {
    const { errorHandler } = config;
    if (!errorHandler) return;

    const { showErrorMessage, customHandler, ignoreErrors } = errorHandler;

    // 自定义错误处理
    if (customHandler) {
      customHandler(error);
      return;
    }

    // 忽略特定错误
    if (ignoreErrors?.includes(error.code)) {
      return;
    }

    // 显示错误消息
    if (showErrorMessage) {
      message.error(error.message);
    }
  }

  /**
   * 发送请求
   */
  async request<T = any>(config: RequestConfig): Promise<T> {
    try {
      // 处理节流
      if (config.enableThrottle) {
        const throttled = throttle(
          () => this.instance.request(config),
          config.throttleInterval || 1000
        );
        return await throttled();
      }

      return await this.instance.request(config);
    } catch (error) {
      this.handleError(error, config);
      throw error;
    }
  }

  /**
   * GET请求
   */
  get<T = any>(url: string, config: RequestConfig = {}): Promise<T> {
    return this.request({ ...config, method: 'GET', url });
  }

  /**
   * POST请求
   */
  post<T = any>(url: string, data?: any, config: RequestConfig = {}): Promise<T> {
    return this.request({ ...config, method: 'POST', url, data });
  }

  /**
   * PUT请求
   */
  put<T = any>(url: string, data?: any, config: RequestConfig = {}): Promise<T> {
    return this.request({ ...config, method: 'PUT', url, data });
  }

  /**
   * DELETE请求
   */
  delete<T = any>(url: string, config: RequestConfig = {}): Promise<T> {
    return this.request({ ...config, method: 'DELETE', url });
  }

  /**
   * 下载文件
   */
  download(url: string, config: RequestConfig = {}): Promise<DownloadResponse> {
    return this.request({
      ...config,
      method: 'GET',
      url,
      responseType: 'blob',
      isDownload: true,
    });
  }
}

// 导出请求实例
export const request = new Request();
export default request;