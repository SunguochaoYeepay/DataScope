/**
 * 本地存储工具类
 */
export class Storage {
  private prefix: string;

  constructor(prefix: string = 'datascope_') {
    this.prefix = prefix;
  }

  /**
   * 获取完整的key
   */
  private getKey(key: string): string {
    return `${this.prefix}${key}`;
  }

  /**
   * 设置存储项
   */
  set<T>(key: string, value: T): void {
    const fullKey = this.getKey(key);
    try {
      const stringValue = JSON.stringify(value);
      localStorage.setItem(fullKey, stringValue);
    } catch (error) {
      console.error(`Error setting localStorage key "${fullKey}":`, error);
    }
  }

  /**
   * 获取存储项
   */
  get<T>(key: string, defaultValue: T | null = null): T | null {
    const fullKey = this.getKey(key);
    try {
      const item = localStorage.getItem(fullKey);
      if (item === null) return defaultValue;
      return JSON.parse(item);
    } catch (error) {
      console.error(`Error getting localStorage key "${fullKey}":`, error);
      return defaultValue;
    }
  }

  /**
   * 删除存储项
   */
  remove(key: string): void {
    const fullKey = this.getKey(key);
    try {
      localStorage.removeItem(fullKey);
    } catch (error) {
      console.error(`Error removing localStorage key "${fullKey}":`, error);
    }
  }

  /**
   * 清除所有存储项
   */
  clear(): void {
    try {
      Object.keys(localStorage).forEach(key => {
        if (key.startsWith(this.prefix)) {
          localStorage.removeItem(key);
        }
      });
    } catch (error) {
      console.error('Error clearing localStorage:', error);
    }
  }
}

// 导出默认实例
export const storage = new Storage();
export default storage;