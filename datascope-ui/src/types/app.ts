/**
 * 应用主题
 */
export type Theme = 'light' | 'dark';

/**
 * 应用语言
 */
export type Language = 'zh-CN' | 'en-US';

/**
 * 应用状态
 */
export interface AppState {
  /** 是否折叠侧边栏 */
  sidebarCollapsed: boolean;
  /** 主题 */
  theme: Theme;
  /** 语言 */
  language: Language;
  /** 是否显示设置抽屉 */
  showSettings: boolean;
  /** 是否显示标签页 */
  showTabs: boolean;
  /** 页面加载状态 */
  loading: boolean;
  /** 全局消息 */
  message: string | null;
}