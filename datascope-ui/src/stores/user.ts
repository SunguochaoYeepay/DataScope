import { defineStore } from 'pinia';
import type { UserInfo, UserSettings, UserState } from '@/types/user';
import { storage } from '@/utils/storage';
import request from '@/utils/request';
import { login, logout } from '@/api/auth';

const defaultSettings: UserSettings = {
  theme: 'light',
  language: 'zh-CN',
  tableSize: 'medium',
  showWelcome: true,
};

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userInfo: storage.get('userInfo'),
    settings: storage.get('userSettings') || defaultSettings,
    token: storage.get('token'),
  }),

  getters: {
    isLoggedIn(): boolean {
      return !!this.token && !!this.userInfo;
    },

    username(): string {
      return this.userInfo?.username || '';
    },

    hasRole(): (role: string) => boolean {
      return (role: string) => this.userInfo?.roles.includes(role) || false;
    },

    hasPermission(): (permission: string) => boolean {
      return (permission: string) =>
        this.userInfo?.permissions.includes(permission) || false;
    },
  },

  actions: {
    /**
     * 设置用户信息
     */
    setUserInfo(userInfo: UserInfo | null) {
      this.userInfo = userInfo;
      if (userInfo) {
        storage.set('userInfo', userInfo);
      } else {
        storage.remove('userInfo');
      }
    },

    /**
     * 设置用户设置
     */
    setSettings(settings: Partial<UserSettings>) {
      this.settings = { ...this.settings, ...settings };
      storage.set('userSettings', this.settings);
    },

    /**
     * 设置token
     */
    setToken(token: string | null) {
      this.token = token;
      if (token) {
        storage.set('token', token);
      } else {
        storage.remove('token');
      }
    },

    /**
     * 登录
     */
    async login(username: string, password: string) {
      try {
        const { token, userInfo } = await login(username, password);
        this.setToken(token);
        this.setUserInfo(userInfo);
      } catch (error) {
        this.resetState();
        throw error;
      }
    },

    /**
     * 获取用户信息
     */
    async fetchUserInfo() {
      try {
        const userInfo = await request.get('/auth/user-info');
        this.setUserInfo(userInfo);
        return userInfo;
      } catch (error) {
        this.logout();
        throw error;
      }
    },

    /**
     * 登出
     */
    async logout() {
      try {
        await logout();
      } finally {
        this.resetState();
      }
    },

    /**
     * 重置状态
     */
    resetState() {
      this.setToken(null);
      this.setUserInfo(null);
      this.setSettings(defaultSettings);
      storage.clear();
    },
  },
});