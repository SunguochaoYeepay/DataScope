import { defineStore } from 'pinia';
import type { AppState, Theme, Language } from '@/types/app';
import { storage } from '@/utils/storage';

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    sidebarCollapsed: false,
    theme: 'light',
    language: 'zh-CN',
    showSettings: false,
    showTabs: true,
    loading: false,
    message: null,
  }),

  getters: {
    isDarkTheme(): boolean {
      return this.theme === 'dark';
    },
  },

  actions: {
    /**
     * 设置侧边栏状态
     */
    setSidebarCollapsed(collapsed: boolean) {
      this.sidebarCollapsed = collapsed;
      storage.set('sidebarCollapsed', this.sidebarCollapsed);
    },

    /**
     * 切换侧边栏状态
     */
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed;
      storage.set('sidebarCollapsed', this.sidebarCollapsed);
    },

    /**
     * 设置主题
     */
    setTheme(theme: Theme) {
      this.theme = theme;
      storage.set('theme', theme);
      // 更新 HTML 的 data-theme 属性
      document.documentElement.setAttribute('data-theme', theme);
    },

    /**
     * 设置语言
     */
    setLanguage(language: Language) {
      this.language = language;
      storage.set('language', language);
      // 更新 HTML 的 lang 属性
      document.documentElement.setAttribute('lang', language);
    },

    /**
     * 切换设置抽屉
     */
    toggleSettings() {
      this.showSettings = !this.showSettings;
    },

    /**
     * 切换标签页显示
     */
    toggleTabs() {
      this.showTabs = !this.showTabs;
      storage.set('showTabs', this.showTabs);
    },

    /**
     * 设置加载状态
     */
    setLoading(loading: boolean) {
      this.loading = loading;
    },

    /**
     * 设置全局消息
     */
    setMessage(message: string | null) {
      this.message = message;
    },

    /**
     * 初始化应用状态
     */
    initApp() {
      // 从本地存储加载设置
      const theme = localStorage.getItem('theme') as Theme;
      const language = localStorage.getItem('language') as Language;
      const showTabs = localStorage.getItem('showTabs');

      if (theme) {
        this.theme = theme;
      }
      if (language) {
        this.language = language;
      }
      if (showTabs !== null) {
        this.showTabs = showTabs === 'true';
      }
    },
  },
});