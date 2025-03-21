export interface QueryHistory {
  id: string;
  queryId: string;
  parameters: string;
  executionTime: number;
  affectedRows: number;
  status: string;
  errorMessage?: string;
  executionIp: string;
  createdAt: string;
  createdBy: string;
}

export interface QueryStats {
  totalExecutions: number;
  averageExecutionTime: number;
  successRate: number;
  totalAffectedRows: number;
  maxExecutionTime: number;
  minExecutionTime: number;
  successCount: number;
  failureCount: number;
}

export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

export interface QueryHistoryStats {
  total_executions: number;
  avg_execution_time: number;
  max_execution_time: number;
  min_execution_time: number;
  total_affected_rows: number;
  success_count: number;
  failed_count: number;
}