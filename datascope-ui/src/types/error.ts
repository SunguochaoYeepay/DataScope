/**
 * 错误码定义
 */
export enum ErrorCode {
  SUCCESS = 200,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  INTERNAL_ERROR = 500,
  BAD_REQUEST = 400,
  TIMEOUT = 408,
  RATE_LIMIT = 429,
}

/**
 * 自定义错误类
 */
export class ApiError extends Error {
  constructor(
    public code: number,
    message: string,
    public data?: any
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

/**
 * 错误处理配置
 */
export interface ErrorHandlerConfig {
  /** 是否显示错误消息 */
  showErrorMessage?: boolean;
  /** 自定义错误处理 */
  customHandler?: (error: ApiError) => void;
  /** 忽略的错误码 */
  ignoreErrors?: number[];
}