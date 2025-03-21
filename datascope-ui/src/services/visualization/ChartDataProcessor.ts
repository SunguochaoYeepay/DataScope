import type { ChartType } from '@/types/visualization'

export interface DataPoint {
  [key: string]: any
}

export interface ProcessedData {
  series: Array<{
    name: string
    data: any[]
    type?: string
    stack?: string
  }>
  xAxis?: {
    type: string
    data?: any[]
  }
  yAxis?: {
    type: string
  }
}

export interface ProcessConfig {
  chartType: ChartType
  xField?: string
  yField?: string
  seriesField?: string
  valueField?: string
  sortBy?: 'asc' | 'desc'
  limit?: number
  aggregation?: 'sum' | 'avg' | 'count' | 'max' | 'min'
  stack?: boolean
}

export class ChartDataProcessor {
  /**
   * 处理图表数据
   * @param rawData 原始数据
   * @param config 处理配置
   * @returns 处理后的数据
   */
  static process(rawData: DataPoint[], config: ProcessConfig): ProcessedData {
    if (!rawData || rawData.length === 0) {
      return { series: [] }
    }

    switch (config.chartType) {
      case 'line':
      case 'bar':
      case 'area':
        return this.processAxisChart(rawData, config)
      case 'pie':
        return this.processPieChart(rawData, config)
      case 'scatter':
        return this.processScatterChart(rawData, config)
      case 'radar':
        return this.processRadarChart(rawData, config)
      case 'funnel':
        return this.processFunnelChart(rawData, config)
      case 'heatmap':
        return this.processHeatmapChart(rawData, config)
      default:
        throw new Error(`Unsupported chart type: ${config.chartType}`)
    }
  }

  /**
   * 处理坐标轴类图表数据（折线图、柱状图、面积图）
   */
  private static processAxisChart(rawData: DataPoint[], config: ProcessConfig): ProcessedData {
    const { xField, yField, seriesField, aggregation, stack } = config
    if (!xField || !yField) {
      throw new Error('xField and yField are required for axis charts')
    }

    // 获取所有唯一的 X 轴值
    const xValues = [...new Set(rawData.map(item => item[xField]))]

    // 如果有系列字段，按系列分组处理
    if (seriesField) {
      const seriesMap = new Map<string, Map<any, number>>()
      
      // 按系列分组并聚合数据
      rawData.forEach(item => {
        const seriesName = item[seriesField]
        const xValue = item[xField]
        const yValue = Number(item[yField])

        if (!seriesMap.has(seriesName)) {
          seriesMap.set(seriesName, new Map())
        }
        
        const seriesData = seriesMap.get(seriesName)!
        if (seriesData.has(xValue)) {
          const currentValue = seriesData.get(xValue)!
          seriesData.set(xValue, this.aggregate(currentValue, yValue, aggregation))
        } else {
          seriesData.set(xValue, yValue)
        }
      })

      // 构建系列数据
      const series = Array.from(seriesMap.entries()).map(([name, data]) => ({
        name,
        type: config.chartType,
        stack: stack ? 'total' : undefined,
        data: xValues.map(x => data.get(x) || 0)
      }))

      return {
        series,
        xAxis: {
          type: 'category',
          data: xValues
        },
        yAxis: {
          type: 'value'
        }
      }
    } else {
      // 无系列字段时，直接聚合数据
      const aggregatedData = new Map<any, number>()
      
      rawData.forEach(item => {
        const xValue = item[xField]
        const yValue = Number(item[yField])

        if (aggregatedData.has(xValue)) {
          const currentValue = aggregatedData.get(xValue)!
          aggregatedData.set(xValue, this.aggregate(currentValue, yValue, aggregation))
        } else {
          aggregatedData.set(xValue, yValue)
        }
      })

      return {
        series: [{
          type: config.chartType,
          data: xValues.map(x => aggregatedData.get(x) || 0)
        }],
        xAxis: {
          type: 'category',
          data: xValues
        },
        yAxis: {
          type: 'value'
        }
      }
    }
  }

  /**
   * 处理饼图数据
   */
  private static processPieChart(rawData: DataPoint[], config: ProcessConfig): ProcessedData {
    const { seriesField, valueField } = config
    if (!seriesField || !valueField) {
      throw new Error('seriesField and valueField are required for pie charts')
    }

    // 按系列字段聚合数据
    const aggregatedData = new Map<string, number>()
    
    rawData.forEach(item => {
      const name = item[seriesField]
      const value = Number(item[valueField])

      if (aggregatedData.has(name)) {
        const currentValue = aggregatedData.get(name)!
        aggregatedData.set(name, this.aggregate(currentValue, value, config.aggregation))
      } else {
        aggregatedData.set(name, value)
      }
    })

    // 排序和限制数据量
    let data = Array.from(aggregatedData.entries())
      .map(([name, value]) => ({ name, value }))

    if (config.sortBy) {
      data.sort((a, b) => {
        return config.sortBy === 'desc' ? b.value - a.value : a.value - b.value
      })
    }

    if (config.limit && config.limit < data.length) {
      const others = data.slice(config.limit)
      const othersSum = others.reduce((sum, item) => sum + item.value, 0)
      data = [
        ...data.slice(0, config.limit),
        { name: '其他', value: othersSum }
      ]
    }

    return {
      series: [{
        type: 'pie',
        data
      }]
    }
  }

  /**
   * 处理散点图数据
   */
  private static processScatterChart(rawData: DataPoint[], config: ProcessConfig): ProcessedData {
    const { xField, yField, seriesField } = config
    if (!xField || !yField) {
      throw new Error('xField and yField are required for scatter charts')
    }

    if (seriesField) {
      // 按系列分组
      const seriesMap = new Map<string, Array<[number, number]>>()
      
      rawData.forEach(item => {
        const seriesName = item[seriesField]
        const point: [number, number] = [Number(item[xField]), Number(item[yField])]

        if (!seriesMap.has(seriesName)) {
          seriesMap.set(seriesName, [])
        }
        seriesMap.get(seriesName)!.push(point)
      })

      return {
        series: Array.from(seriesMap.entries()).map(([name, data]) => ({
          name,
          type: 'scatter',
          data
        }))
      }
    } else {
      // 无系列时的处理
      return {
        series: [{
          type: 'scatter',
          data: rawData.map(item => [Number(item[xField]), Number(item[yField])])
        }]
      }
    }
  }

  /**
   * 处理雷达图数据
   */
  private static processRadarChart(rawData: DataPoint[], config: ProcessConfig): ProcessedData {
    const { seriesField, valueField } = config
    if (!seriesField || !valueField) {
      throw new Error('seriesField and valueField are required for radar charts')
    }

    // 获取所有维度
    const dimensions = [...new Set(rawData.map(item => item[seriesField]))]

    // 按系列分组数据
    const seriesMap = new Map<string, number[]>()
    
    rawData.forEach(item => {
      const dimension = item[seriesField]
      const value = Number(item[valueField])
      const dimensionIndex = dimensions.indexOf(dimension)

      if (dimensionIndex === -1) return

      if (!seriesMap.has(dimension)) {
        seriesMap.set(dimension, new Array(dimensions.length).fill(0))
      }
      
      const seriesData = seriesMap.get(dimension)!
      seriesData[dimensionIndex] = value
    })

    return {
      series: [{
        type: 'radar',
        data: Array.from(seriesMap.entries()).map(([name, value]) => ({
          name,
          value
        }))
      }]
    }
  }

  /**
   * 处理漏斗图数据
   */
  private static processFunnelChart(rawData: DataPoint[], config: ProcessConfig): ProcessedData {
    const { seriesField, valueField, sortBy } = config
    if (!seriesField || !valueField) {
      throw new Error('seriesField and valueField are required for funnel charts')
    }

    // 聚合数据
    const aggregatedData = new Map<string, number>()
    
    rawData.forEach(item => {
      const name = item[seriesField]
      const value = Number(item[valueField])

      if (aggregatedData.has(name)) {
        const currentValue = aggregatedData.get(name)!
        aggregatedData.set(name, this.aggregate(currentValue, value, config.aggregation))
      } else {
        aggregatedData.set(name, value)
      }
    })

    // 转换为数组并排序
    let data = Array.from(aggregatedData.entries())
      .map(([name, value]) => ({ name, value }))

    if (sortBy) {
      data.sort((a, b) => {
        return sortBy === 'desc' ? b.value - a.value : a.value - b.value
      })
    }

    return {
      series: [{
        type: 'funnel',
        data
      }]
    }
  }

  /**
   * 处理热力图数据
   */
  private static processHeatmapChart(rawData: DataPoint[], config: ProcessConfig): ProcessedData {
    const { xField, yField, valueField } = config
    if (!xField || !yField || !valueField) {
      throw new Error('xField, yField and valueField are required for heatmap charts')
    }

    // 获取 x 和 y 轴的唯一值
    const xValues = [...new Set(rawData.map(item => item[xField]))]
    const yValues = [...new Set(rawData.map(item => item[yField]))]

    // 构建热力图数据
    const data = rawData.map(item => [
      xValues.indexOf(item[xField]),
      yValues.indexOf(item[yField]),
      Number(item[valueField])
    ])

    return {
      series: [{
        type: 'heatmap',
        data
      }],
      xAxis: {
        type: 'category',
        data: xValues
      },
      yAxis: {
        type: 'category',
        data: yValues
      }
    }
  }

  /**
   * 数据聚合处理
   */
  private static aggregate(current: number, value: number, type?: string): number {
    switch (type) {
      case 'sum':
        return current + value
      case 'avg':
        return (current + value) / 2
      case 'max':
        return Math.max(current, value)
      case 'min':
        return Math.min(current, value)
      case 'count':
        return current + 1
      default:
        return current + value
    }
  }
}