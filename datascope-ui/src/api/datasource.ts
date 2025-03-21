import type { Datasource, DatasourceFormData, ApiResponse, PaginatedResponse } from '@/types/datasource'

const API_BASE = '/api/datasources'

// 获取数据源列表
export async function getDatasources(params: {
  page?: number
  pageSize?: number
  search?: string
}): Promise<ApiResponse<PaginatedResponse<Datasource>>> {
  const searchParams = new URLSearchParams()
  if (params.page) searchParams.append('page', params.page.toString())
  if (params.pageSize) searchParams.append('pageSize', params.pageSize.toString())
  if (params.search) searchParams.append('search', params.search)

  const response = await fetch(`${API_BASE}?${searchParams.toString()}`)
  if (!response.ok) {
    throw new Error('Failed to fetch datasources')
  }
  return response.json()
}

// 创建数据源
export async function createDatasource(data: DatasourceFormData): Promise<ApiResponse<Datasource>> {
  const response = await fetch(API_BASE, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })
  if (!response.ok) {
    throw new Error('Failed to create datasource')
  }
  return response.json()
}

// 更新数据源
export async function updateDatasource(
  id: number,
  data: DatasourceFormData
): Promise<ApiResponse<Datasource>> {
  const response = await fetch(`${API_BASE}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })
  if (!response.ok) {
    throw new Error('Failed to update datasource')
  }
  return response.json()
}

// 删除数据源
export async function deleteDatasource(id: number): Promise<ApiResponse<void>> {
  const response = await fetch(`${API_BASE}/${id}`, {
    method: 'DELETE',
  })
  if (!response.ok) {
    throw new Error('Failed to delete datasource')
  }
  return response.json()
}

// 测试数据源连接
export async function testDatasourceConnection(id: number): Promise<ApiResponse<boolean>> {
  const response = await fetch(`${API_BASE}/${id}/test`, {
    method: 'POST',
  })
  if (!response.ok) {
    throw new Error('Failed to test datasource connection')
  }
  return response.json()
}

// 获取数据源详情
export async function getDatasourceById(id: number): Promise<ApiResponse<Datasource>> {
  const response = await fetch(`${API_BASE}/${id}`)
  if (!response.ok) {
    throw new Error('Failed to fetch datasource')
  }
  return response.json()
}