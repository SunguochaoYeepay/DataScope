// 可视化类型定义
export enum ChartType {
  Line = 'line',
  Bar = 'bar',
  Pie = 'pie',
  Scatter = 'scatter',
  Table = 'table'
}

// 图表配置接口
export interface ChartConfig {
  type: ChartType
  title: string
  description?: string
  datasetId: number
  options: {
    xAxis?: {
      field: string
      label?: string
    }
    yAxis?: {
      field: string
      label?: string
    }
    series?: {
      field: string
      label?: string
      type?: string
    }[]
    color?: string[]
    legend?: boolean
    grid?: {
      show: boolean
    }
  }
}

// 可视化项目接口
export interface Visualization {
  id: number
  name: string
  type: ChartType
  description?: string
  datasetId: number
  config: ChartConfig
  createdAt: string
  updatedAt: string
}

// 数据集预览数据接口
export interface DatasetPreview {
  fields: string[]
  data: Record<string, any>[]
}