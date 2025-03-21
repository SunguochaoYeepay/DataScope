export interface SQLParseRequest {
  sql: string;
  dataSourceId: string;
}

export interface SQLParseResult {
  tables: string[];
  columns: string[];
  type: string;
  where: string[];
  orderBy: string[];
  groupBy: string[];
  limit?: number;
  offset?: number;
  isValid: boolean;
  errorMessage?: string;
}

export interface TableInfo {
  name: string;
  columns: ColumnInfo[];
}

export interface ColumnInfo {
  name: string;
  type: string;
  nullable: boolean;
  primaryKey: boolean;
  comment?: string;
}